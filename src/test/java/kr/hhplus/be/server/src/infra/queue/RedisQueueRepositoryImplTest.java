package kr.hhplus.be.server.src.infra.queue;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.infra.persistence.queue.RedisQueueRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisQueueRepositoryImplTest {

    @InjectMocks
    private RedisQueueRepositoryImpl redisQueueRepositoryImpl; // save() 메서드 포함한 클래스

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private ZSetOperations<String, String> zSetOps;


    // 1. token:token_value
    // 2. user:1:token
    // 3. queue:global
    @DisplayName("기존 토큰이 없는 최초 토큰 발행 요청 테스트로 3개의 redis key를 저장한다.")
    @Test
    void newTokenIssueTest() {

        //given
        Long mockUserId = 1L;
        Queue mockQueue = Queue.newToken(mockUserId);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

        // when
        redisQueueRepositoryImpl.save(mockQueue);

        // then
        verify(hashOps).putAll(
                eq("token:" + mockQueue.getTokenValue()),
                argThat(map -> map.containsKey("user_id") && map.containsKey("token_status"))
        );

        verify(valueOps).set(
                eq("user:" + mockUserId + ":token"),
                eq(mockQueue.getTokenValue())
        );

        verify(zSetOps).add(
                eq("queue:global"),
                eq("token:" + mockQueue.getTokenValue()),
                anyDouble()
        );

    }

    @DisplayName("만료 시간이 지나지 않은 살아있는 Active 토큰은 시간 연장된다.")
    @Test
    void activeTokenIssueTest(){

        //given
        Long mockUserId = 1L;
        Queue mockExistingActiveQueue = Queue.newToken(mockUserId);
        mockExistingActiveQueue.setTokenStatus(TokenStatus.ACTIVE);
        mockExistingActiveQueue.refreshToken();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        // when
        redisQueueRepositoryImpl.save(mockExistingActiveQueue);

        // then
        verify(hashOps).putAll(
                eq("token:" + mockExistingActiveQueue.getTokenValue()),
                argThat((Map<String, String> map) -> {
                    long issuedAt = Long.parseLong(map.get("issued_at"));
                    long expiredAt = Long.parseLong(map.get("expired_at"));
                    return map.containsKey("user_id")
                            && map.containsKey("token_status")
                            && expiredAt > issuedAt;
                })
        );

        verify(valueOps).set(
                eq("user:" + mockUserId + ":token"),
                eq(mockExistingActiveQueue.getTokenValue())
        );

        verify(zSetOps, never()).add(anyString(), anyString(), anyDouble());
    }

    @DisplayName("만료 시간이 지난 expired 토큰 제거되고 신규로 토큰 발행된다.")
    @Test
    void expiredTokenIssueTest() {

        //given
        String mockOldTokenValue = "mockOldTokenValue12356";

        Long mockUserId = 1L;
        Queue mockExistingExpiredQueue = Queue.newToken(mockUserId);
        mockExistingExpiredQueue.setTokenStatus(TokenStatus.EXPIRED);

        Queue mockNewToken = Queue.newToken(mockUserId);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);

        // when
        redisQueueRepositoryImpl.save(mockNewToken);

        // then
        verify(hashOps).putAll(
                eq("token:" + mockNewToken.getTokenValue()),
                argThat((Map<String, String> map) -> {
                    long issuedAt = Long.parseLong(map.get("issued_at"));
                    long expiredAt = Long.parseLong(map.get("expired_at"));
                    return map.containsKey("user_id")
                            && map.containsKey("token_status")
                            && expiredAt > issuedAt;
                })
        );

        verify(valueOps).set(
                eq("user:" + mockUserId + ":token"),
                eq(mockNewToken.getTokenValue())
        );

        verify(zSetOps).add(
                eq("queue:global"),
                eq("token:" + mockNewToken.getTokenValue()),
                anyDouble()
        );
    }

    @DisplayName("스케줄러로 ready -> active 변경 시 상태 및 시간만 수정된다.")
    @Test
    void scheduleTokenIssueTest() {

        //given
        Long mockUserId = 1L;
        Queue readyToActiveQueue = Queue.newToken(mockUserId);
        readyToActiveQueue.setTokenStatus(TokenStatus.ACTIVE);
        readyToActiveQueue.refreshToken();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        // when
        redisQueueRepositoryImpl.save(readyToActiveQueue);

        // then
        verify(hashOps).putAll(
                eq("token:" + readyToActiveQueue.getTokenValue()),
                argThat((Map<String, String> map) -> {
                    long issuedAt = Long.parseLong(map.get("issued_at"));
                    long expiredAt = Long.parseLong(map.get("expired_at"));
                    return map.containsKey("user_id")
                            && map.containsKey("token_status")
                            && expiredAt > issuedAt;
                })
        );

        verify(valueOps).set(
                eq("user:" + mockUserId + ":token"),
                eq(readyToActiveQueue.getTokenValue())
        );
    }

    @DisplayName("userId에 대한 토큰이 존재하지 않으면 Optional.empty()를 반환한다.")
    @Test
    void findByUserIdAndTokenStatus_tokenValueNotFound() {

        //given
        Long userId = 1L;

        // tokenValue가 없을 경우
        when(valueOps.get("user:" + userId + ":token")).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        //when
        Optional<Queue> result = redisQueueRepositoryImpl.findByUserIdAndTokenStatus(userId, TokenStatus.ACTIVE);

        //then
        assertFalse(result.isPresent());
    }

    @DisplayName("tokenMap이 비어 있거나 존재하지 않으면 Optional.empty()를 반환한다.")
    @Test
    void findByUserIdAndTokenStatus_tokenMapNotFound() {

        //given
        Long userId = 1L;
        String tokenValue = "abc123";
        TokenStatus expectedStatus = TokenStatus.READY;

        // userId에 해당하는 tokenValue를 반환
        when(valueOps.get("user:" + userId + ":token")).thenReturn(tokenValue);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        // 빈 tokenMap을 반환
        when(hashOps.entries("token:" + tokenValue)).thenReturn(new HashMap<>());

        //when
        Optional<Queue> result = redisQueueRepositoryImpl.findByUserIdAndTokenStatus(userId, expectedStatus);

        //then
        assertFalse(result.isPresent());
    }

    @DisplayName("정상적으로 토큰 상태가 일치하면 Queue 객체를 반환한다.")
    @Test
    void findByUserIdAndTokenStatus_success() {

        //given
        Long userId = 1L;
        String tokenValue = "abc123";
        TokenStatus expectedStatus = TokenStatus.READY;

        when(valueOps.get("user:" + userId + ":token")).thenReturn(tokenValue);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        Map<Object, Object> mockTokenMap = new HashMap<>();
        mockTokenMap.put("user_id", userId.toString());
        mockTokenMap.put("token_status", expectedStatus.getCode());
        mockTokenMap.put("issued_at", String.valueOf(System.currentTimeMillis()));
        mockTokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() + 10000));

        when(hashOps.entries("token:" + tokenValue)).thenReturn(mockTokenMap);

        //when
        Optional<Queue> result = redisQueueRepositoryImpl.findByUserIdAndTokenStatus(userId, expectedStatus);

        //then
        assertTrue(result.isPresent());
        assertEquals(expectedStatus, result.get().getTokenStatus());
        assertEquals(tokenValue, result.get().getTokenValue());
    }

    @DisplayName("Redis에 토큰이 없으면 Optional.empty()를 반환한다.")
    @Test
    void findByTokenValueAndTokenStatus_tokenNotFound() {
        // given
        String tokenValue = "abc123";
        TokenStatus expectedStatus = TokenStatus.READY;

        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries("token:" + tokenValue)).thenReturn(Collections.emptyMap());

        // when
        Optional<Queue> result = redisQueueRepositoryImpl.findByTokenValueAndTokenStatus(tokenValue, expectedStatus);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("특정 토큰_value & 토큰_status로 대기 토큰 조회하여 반환한다.")
    @Test
    void findByTokenValueAndTokenStatus_success() {

        //given
        String tokenValue = "abc123";
        TokenStatus expectedStatus = TokenStatus.ACTIVE;

        Map<Object, Object> mockTokenMap = new HashMap<>();
        mockTokenMap.put("user_id", "1");
        mockTokenMap.put("token_status", expectedStatus.getCode());
        mockTokenMap.put("issued_at", String.valueOf(System.currentTimeMillis()));
        mockTokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() + 5 * 60 * 1000)); // 5분 후

        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries("token:" + tokenValue)).thenReturn(mockTokenMap);

        //when
        Optional<Queue> result = redisQueueRepositoryImpl.findByTokenValueAndTokenStatus(tokenValue, expectedStatus);

        //then
        assertTrue(result.isPresent());
        assertEquals(expectedStatus, result.get().getTokenStatus());
        assertEquals(tokenValue, result.get().getTokenValue());
    }

    @DisplayName("만료 대상 조회 시 - 만료 시간을 지난 ACTIVE 상태 토큰들을 정상적으로 조회한다.")
    @Test
    void findByExpiredAtBeforeAndTokenStatus_success() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        int limit = 100;

        String tokenValue = "abc123";
        Set<String> expiredTokenSet = Set.of("token:" + tokenValue);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", -Double.MAX_VALUE, nowMillis)).thenReturn(expiredTokenSet);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        Map<Object, Object> mockTokenMap = new HashMap<>();
        mockTokenMap.put("user_id", "1");
        mockTokenMap.put("token_status", TokenStatus.ACTIVE.getCode());
        mockTokenMap.put("issued_at", String.valueOf(System.currentTimeMillis() - 10_000));
        mockTokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() - 1_000)); // expired
        mockTokenMap.put("tokenValue", tokenValue);

        when(hashOps.entries("token:" + tokenValue)).thenReturn(mockTokenMap);

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByExpiredAtBeforeAndTokenStatus(now, TokenStatus.ACTIVE);

        // then
        assertEquals(1, result.size());
        assertEquals(tokenValue, result.get(0).getTokenValue());
        assertEquals(TokenStatus.ACTIVE, result.get(0).getTokenStatus());
    }

    @DisplayName("만료 대상 조회 시 - ZSet 결과가 비어 있는 경우 빈 리스트를 반환한다.")
    @Test
    void findByExpiredAtBeforeAndTokenStatus_whenZSetIsEmpty_returnsEmptyList() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", -Double.MAX_VALUE, nowMillis)).thenReturn(Collections.emptySet());

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByExpiredAtBeforeAndTokenStatus(now, TokenStatus.ACTIVE);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("만료 대상 조회 시 - Hash 값이 비어 있는 경우 해당 토큰은 무시된다.")
    @Test
    void findByExpiredAtBeforeAndTokenStatus_whenHashIsEmpty_tokenIgnored() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

        String tokenValue = "expiredToken123";

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", -Double.MAX_VALUE, nowMillis)).thenReturn(Set.of("token:" + tokenValue));

        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries("token:" + tokenValue)).thenReturn(Collections.emptyMap());

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByExpiredAtBeforeAndTokenStatus(now, TokenStatus.ACTIVE);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @DisplayName("만료 토큰 대상 조회 시 - 만료되지 않고 상태가 READY인 토큰들을 정상적으로 조회한다.")
    @Test
    void findByTokenStatusAndExpiredAtAfter_success() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        int limit = 100;

        String fullTokenKey = "token:abc123";
        String tokenValue = "abc123";

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", nowMillis, Double.MAX_VALUE, 0, limit))
                .thenReturn(Set.of(fullTokenKey));

        Map<Object, Object> mockTokenMap = new HashMap<>();
        mockTokenMap.put("user_id", "1");
        mockTokenMap.put("token_status", "READY");
        mockTokenMap.put("issued_at", String.valueOf(System.currentTimeMillis()));
        mockTokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() + 100000));
        mockTokenMap.put("tokenValue", tokenValue);

        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(fullTokenKey)).thenReturn(mockTokenMap);

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByTokenStatusAndExpiredAtAfter(TokenStatus.READY, now);

        // then
        assertEquals(1, result.size());
        assertEquals(tokenValue, result.get(0).getTokenValue());
        assertEquals(TokenStatus.READY, result.get(0).getTokenStatus());
    }

    @DisplayName("만료 토큰 대상 조회 시 - ZSet 결과가 없으면 빈 리스트를 반환한다.")
    @Test
    void findByTokenStatusAndExpiredAtAfter_emptyZSet_returnsEmptyList() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        int limit = 100;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", nowMillis, Double.MAX_VALUE, 0, limit)).thenReturn(Collections.emptySet());

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByTokenStatusAndExpiredAtAfter(TokenStatus.READY, now);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("Hash 데이터가 비어 있으면 해당 토큰은 무시된다.")
    @Test
    void findByTokenStatusAndExpiredAtAfter_emptyHash_ignored() {
        // given
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        double nowMillis = now.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
        int limit = 100;

        String tokenKey = "token:abc123";

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.rangeByScore("queue:global", nowMillis, Double.MAX_VALUE, 0, limit)).thenReturn(Set.of(tokenKey));

        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(tokenKey)).thenReturn(Collections.emptyMap());

        // when
        List<Queue> result = redisQueueRepositoryImpl.findByTokenStatusAndExpiredAtAfter(TokenStatus.READY, now);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("ZSet 내에서 상태가 일치하는 토큰이 존재하면 해당 Queue를 반환한다.")
    @Test
    void findByTokenStatus_success() {
        // given
        String tokenValue = "abc123";
        String fullTokenKey = "token:" + tokenValue;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.range("queue:global", 0, -1)).thenReturn(Set.of(fullTokenKey));
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        Map<Object, Object> mockTokenMap = new HashMap<>();
        mockTokenMap.put("user_id", "1");
        mockTokenMap.put("token_status", "READY");
        mockTokenMap.put("issued_at", String.valueOf(System.currentTimeMillis()));
        mockTokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() + 100000));
        mockTokenMap.put("tokenValue", tokenValue);

        when(hashOps.entries(fullTokenKey)).thenReturn(mockTokenMap);

        // when
        Queue result = redisQueueRepositoryImpl.findByTokenStatus(TokenStatus.READY);

        // then
        assertNotNull(result);
        assertEquals(TokenStatus.READY, result.getTokenStatus());
        assertEquals(tokenValue, result.getTokenValue());
    }

    @DisplayName("ZSet이 비어 있으면 null을 반환한다.")
    @Test
    void findByTokenStatus_emptyZSet_returnsNull() {
        // given
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.range("queue:global", 0, -1)).thenReturn(Collections.emptySet());

        // when
        Queue result = redisQueueRepositoryImpl.findByTokenStatus(TokenStatus.READY);

        // then
        assertNull(result);
    }

    @DisplayName("상태가 일치하지 않으면 null을 반환한다.")
    @Test
    void findByTokenStatus_statusMismatch_returnsNull() {
        // given
        String tokenValue = "abc123";
        String fullTokenKey = "token:" + tokenValue;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.range("queue:global", 0, -1)).thenReturn(Set.of(fullTokenKey));
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        Map<Object, Object> tokenMap = new HashMap<>();
        tokenMap.put("user_id", "1");
        tokenMap.put("token_status", "ACTIVE"); // <--- 다른 상태
        tokenMap.put("issued_at", String.valueOf(System.currentTimeMillis()));
        tokenMap.put("expired_at", String.valueOf(System.currentTimeMillis() + 100000));
        tokenMap.put("tokenValue", tokenValue);

        when(hashOps.entries(fullTokenKey)).thenReturn(tokenMap);

        // when
        Queue result = redisQueueRepositoryImpl.findByTokenStatus(TokenStatus.READY);

        // then
        assertNull(result);
    }

    @DisplayName("Hash 값이 비어 있으면 해당 토큰은 무시되고 다음으로 진행한다.")
    @Test
    void findByTokenStatus_emptyHash_ignored() {
        // given
        String tokenValue = "abc123";
        String fullTokenKey = "token:" + tokenValue;

        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.range("queue:global", 0, -1)).thenReturn(Set.of(fullTokenKey));
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(fullTokenKey)).thenReturn(Collections.emptyMap());

        // when
        Queue result = redisQueueRepositoryImpl.findByTokenStatus(TokenStatus.READY);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("유저 토큰 값이 존재할 경우 정상 반환")
    void getUserTokenValue_success() {
        // given
        Long userId = 1L;
        String expectedTokenValue = "abc123";

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:" + userId + ":token")).thenReturn(expectedTokenValue);

        // when
        String tokenValue = redisQueueRepositoryImpl.getUserTokenValue(userId);

        // then
        assertEquals(expectedTokenValue, tokenValue);
    }

    @DisplayName("유저 토큰 값이 존재하지 않을 경우 EntityNotFoundException 발생")
    @Test
    void getUserTokenValue_notFound() {
        // given
        Long userId = 1L;
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:" + userId + ":token")).thenReturn(null);

        // when & then
        assertThrows(EntityNotFoundException.class, () -> redisQueueRepositoryImpl.getUserTokenValue(userId));
    }

}