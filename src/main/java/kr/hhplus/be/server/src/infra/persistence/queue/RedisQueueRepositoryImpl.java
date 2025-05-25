package kr.hhplus.be.server.src.infra.persistence.queue;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class RedisQueueRepositoryImpl implements RedisQueueRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String USER_TOKEN_KEY_PREFIX = "user:";
    private final static String TOKEN_HASH_PREFIX = "token:";

    /*
    userId, tokenStatus로 ACTIVE / EXPIRED Token 조회
    해당 유저가 이미 발급 받은 ACTIVE / EXPIRED 상태의 토큰이 있는지 조회

    <토큰 정보 조회 결과>
     key => token : {token_value}
     value(tokenMap) => { user_id : {user_id}, token_status : {token_stauts}, issued_at : {issued_at}, expired_at : {expired_at} }
     */
    @Override
    public Optional<Queue> findByUserIdAndTokenStatus(Long userId, TokenStatus tokenStatus) {
        String tokenValue = redisTemplate.opsForValue().get(USER_TOKEN_KEY_PREFIX + userId + ":token");
        if (tokenValue == null) return Optional.empty();

        Map<Object, Object> tokenMap = redisTemplate.opsForHash().entries(TOKEN_HASH_PREFIX + tokenValue);
        if (tokenMap.isEmpty()) return Optional.empty();
        tokenMap.put("tokenValue", tokenValue);

        Queue queue = mapToQueue(tokenMap);
        return queue.getTokenStatus() == tokenStatus ? Optional.of(queue) : Optional.empty();
    }

    @Override
    public Queue save(Queue queue) {
        // 1. 유저-토큰 맵핑 저장
        redisTemplate.opsForValue().set(
                "user:" + queue.getUserId() + ":token"
                , queue.getTokenValue()
        );

        // 2. 토큰 상세 정보 저장
        Map<String, String> tokenDetails = Map.of(
                "user_id", String.valueOf(queue.getUserId()),
                "token_status", queue.getTokenStatus().name(),
                "issued_at", String.valueOf(queue.getIssuedAt().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()),
                "expired_at", String.valueOf(queue.getExpiredAt().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())
        );
        redisTemplate.opsForHash().putAll("token:" + queue.getTokenValue(), tokenDetails);

        // 3. 글로벌 대기열 추가 (단, ACTIVE 상태일 경우 순위 변경 X)
        if (queue.getTokenStatus() == TokenStatus.READY) {
            redisTemplate.opsForZSet().add(
                    "queue:global",
                    "token:" + queue.getTokenValue(),
                    queue.getExpiredAt().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
            );
        }

        return queue;
    }

    /*
    특정 토큰_value, 토큰 상태로 대기 토큰 조회

    <토큰 정보 조회 결과>
    key => token:{token_value}
    value => { user_id : {user_id}, token_status : {token_stauts}, issued_at : {issued_at}, expired_at : {expired_at} }
     */
    @Override
    public Optional<Queue> findByTokenValueAndTokenStatus(String tokenValue, TokenStatus tokenStatus) {
        // Redis에서 해당 token_value 로 토큰 정보 조회
        String tokenKey = TOKEN_HASH_PREFIX + tokenValue;

        // Redis Hash에서 토큰 정보를 조회
        Map<Object, Object> tokenMap;
        try {
            tokenMap = redisTemplate.opsForHash().entries(tokenKey);
        } catch (Exception e) {
            return Optional.empty(); // Redis 조회 실패 시 바로 empty
        }

        String tokenMapStatus = (String) tokenMap.get("token_status");
        if (tokenMapStatus == null) {
            return Optional.empty(); // 상태 정보 자체가 없으면 유효하지 않음
        }


        // redis의 token 상태가 active 상태인지 확인
        if (!tokenStatus.toString().equals(tokenMapStatus)) {
            throw new EntityNotFoundException("해당 토큰은 " + tokenStatus + " 상태가 아닙니다.");
        }

        // 토큰 정보로 Queue 객체 생성 및 반환
        return Optional.ofNullable(Queue.builder()
                .userId(Long.valueOf((String) tokenMap.get("user_id")))
                .tokenStatus(TokenStatus.valueOf(tokenMapStatus))
                .issuedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong((String) tokenMap.get("issued_at"))), ZoneId.of("UTC")))
                .expiredAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong((String) tokenMap.get("expired_at"))), ZoneId.of("UTC")))
                .tokenValue(tokenValue)
                .build());
    }

    @Override
    public List<Queue> findByExpiredAtBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus) {
        // 현재 시간을 밀리초로 변환
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        Set<String> expiredTokenValues = redisTemplate.opsForZSet().rangeByScore(
                "queue:global",
                -Double.MAX_VALUE, // - 부호 주의(가장 작은 값 의미)
                nowMillis
        );

        List<Queue> expiredQueues = new ArrayList<>();
        for (String tokenValue : expiredTokenValues) {
            Map<Object, Object> tokenMap = redisTemplate.opsForHash().entries(tokenValue);
            if (!tokenMap.isEmpty()) {
                tokenMap.put("tokenValue", tokenValue.substring("token:".length()));
                Queue queue = mapToQueue(tokenMap);
                if (queue.getTokenStatus() == tokenStatus) {
                    expiredQueues.add(queue);
                }
            }
        }

        return expiredQueues;
    }

    @Override
    public List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime now) {
        // 현재 시간을 밀리초로 변환하여 double 타입으로 변환
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        int limit = 100; // 대기열 크기
        // ZSet에서 expiredAt이 nowMillis 이후인, READY 상태의 토큰을 조회
        Set<String> readyTokenValues = redisTemplate.opsForZSet().rangeByScore(
                "queue:global",
                nowMillis,
                Double.MAX_VALUE,
                0,
                limit  //대기열 ready -> active 변환 개수 지정 : 상위 n개만 조회
        );

        List<Queue> readyQueues = new ArrayList<>();
        for (String tokenValue : readyTokenValues) {
            Map<Object, Object> tokenMap = redisTemplate.opsForHash().entries(tokenValue);
            if (tokenMap != null && !tokenMap.isEmpty()) {
                tokenMap.put("tokenValue", tokenValue.substring("token:".length()));
                Queue queue = mapToQueue(tokenMap);
                if (queue.getTokenStatus() == tokenStatus) {
                    readyQueues.add(queue);
                }
            }
        }

        return readyQueues;
    }

    /*
    zset의 토큰 값을 기반으로 해당 hash의 토큰 상태 find
     */
    @Override
    public Queue findByTokenStatus(TokenStatus tokenStatus) {
        Set<String> tokenValues = redisTemplate.opsForZSet().range("queue:global", 0, -1);

        if (tokenValues == null || tokenValues.isEmpty()) {
            return null;
        }

        for (String tokenKey : tokenValues) {
            String tokenValue = tokenKey.replace("token:", "");
            Map<Object, Object> tokenMap = redisTemplate.opsForHash().entries("token:" + tokenValue);

            if (tokenMap != null && !tokenMap.isEmpty()) {
                Queue queue = mapToQueue(tokenMap);

                if (queue.getTokenStatus() == tokenStatus) {
                    return queue;
                }
            }
        }
        // 일치하는 토큰이 없을 경우
        return null;
    }

    @Override
    public void removeExpiredQueue(Queue expiredQueue) {

        String tokenValue = expiredQueue.getTokenValue();
        Long userId = expiredQueue.getUserId();

        // 1. 유저-토큰 맵핑 제거
        redisTemplate.delete("user:" + userId + ":token");

        // 2. 토큰 상세 정보 제거
        redisTemplate.delete("token:" + tokenValue);

        // 3. 전역 대기열 ZSet 에서 제거
        redisTemplate.opsForZSet().remove("queue:global", "token:" + tokenValue);
    }

    @Override
    public String getUserTokenValue(Long userId) {
        String tokenValue = redisTemplate.opsForValue().get("user:" + userId + ":token");
        if (tokenValue == null) {
            throw new EntityNotFoundException("해당 유저의 토큰이 존재하지 않습니다.");
        }
        return tokenValue;
    }

    @Override
    public Set<String> getReadyTokens(String tokenValue, LocalDateTime now) {
        long nowMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Set<String> topTokens = redisTemplate.opsForZSet().rangeByScore("queue:global", nowMillis, Double.MAX_VALUE);

        if (topTokens == null || topTokens.isEmpty()) {
            return Collections.emptySet();
        }

        //Ready 상태인 토큰만 대기열 순번의 대상이므로 filter.
        Set<String> topReadyTokens = new LinkedHashSet<>();
        for (String tokenKey : topTokens) {
            String pureToken = tokenKey.substring("token:".length());
            Map<Object, Object> tokenMap = redisTemplate.opsForHash().entries("token:" + pureToken);

            if (!tokenMap.isEmpty()) {
                String status = (String) tokenMap.get("token_status");
                if ("READY".equals(status)) {
                    topReadyTokens.add(tokenKey);
                }
            }
        }
        return topReadyTokens;
    }

    private Queue mapToQueue(Map<Object, Object> map) {
        return Queue.builder()
                .userId(Long.valueOf((String) map.get("user_id")))
                .tokenStatus(TokenStatus.valueOf((String) map.get("token_status")))
                .issuedAt(Instant.ofEpochMilli(Long.parseLong((String) map.get("issued_at")))
                        .atZone(ZoneId.of("UTC")).toLocalDateTime())
                .expiredAt(Instant.ofEpochMilli(Long.parseLong((String) map.get("expired_at")))
                        .atZone(ZoneId.of("UTC")).toLocalDateTime())
                .tokenValue(extractTokenValue(map))
                .build();
    }

    /*
    token_value 추출
     */
    private String extractTokenValue(Map<Object, Object> map) {
        String tokenValue =  map.containsKey("tokenValue") ? (String) map.get("tokenValue") : UUID.randomUUID().toString();

        return tokenValue;
    }
}
