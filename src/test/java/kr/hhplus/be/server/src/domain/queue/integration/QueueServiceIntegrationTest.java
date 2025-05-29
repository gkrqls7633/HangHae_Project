package kr.hhplus.be.server.src.domain.queue.integration;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueResponse;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class QueueServiceIntegrationTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueTransactionHelper queueTransactionHelper;

    private QueueRequest queueRequest;

    @Autowired
    private RedisQueueRepository redisQueueRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Nested
    class WithSetup {

        @BeforeEach
        void setUp() {
            queueRequest = queueTransactionHelper.setupTestData();
        }

        @DisplayName("유저 정보 체크 후 활성화 중인 토큰 갱신 처리한다.")
        @Test
        void issueQueueToken() {

            //given
            Long userId =  queueRequest.getUserId();

            //when
            ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
            LocalDateTime now = LocalDateTime.now();


            // then: Consumer 처리 이후 Redis 상태 검증
            Awaitility.await()
                    .atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        // 1. user:{userId} 키에서 token_value 조회
                        String tokenValue = redisTemplate.opsForValue().get("user:" + userId + ":token");
                        assertNotNull(tokenValue, "user:{userId} 키에서 토큰 값을 찾을 수 없음");

                        // 2. token:{tokenValue} 해시에서 token_status, issued_at, expired_at 조회
                        String tokenStatusStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "token_status");
                        assertNotNull(tokenStatusStr, "token:{tokenValue} 해시에서 token_status를 찾을 수 없음");

                        TokenStatus tokenStatus = TokenStatus.valueOf(tokenStatusStr);
                        assertEquals(TokenStatus.ACTIVE, tokenStatus, "토큰 상태가 ACTIVE여야 함");

                        String issuedAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "issued_at");
                        String expiredAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "expired_at");

                        assertNotNull(issuedAtMillisStr, "issued_at이 존재해야 함");
                        assertNotNull(expiredAtMillisStr, "expired_at이 존재해야 함");

                        LocalDateTime issuedAt = Instant.ofEpochMilli(Long.parseLong(issuedAtMillisStr))
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDateTime();

                        LocalDateTime expiredAt = Instant.ofEpochMilli(Long.parseLong(expiredAtMillisStr))
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDateTime();

                        Duration duration = Duration.between(issuedAt, expiredAt);
                        assertEquals(5, duration.toMinutes(), "발급 시간과 만료 시간 차이는 5분이어야 함");
                    });
        }
    }

    @DisplayName("유저 정보 체크 후 토큰 신규 발급 처리한다.")
    @Test
    void issueNewQueueToken() {

        QueueRequest queueRequest = queueTransactionHelper.setupTestDataWithNoQueue();

        //given
        Long userId = queueRequest.getUserId();

        //when
        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
        LocalDateTime now = LocalDateTime.now();

        // then: 실제로 토큰이 Redis 에 들어갔는지 polling 으로 확인
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // 1. user:{userId} 키에서 token_value 조회
                    String tokenValue = redisTemplate.opsForValue().get("user:" + userId + ":token");
                    assertNotNull(tokenValue, "user:{userId} 키에서 토큰 값을 찾을 수 없음");

                    // 2. token:{tokenValue} 해시에서 token_status, issued_at, expired_at 조회
                    String tokenStatusStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "token_status");
                    assertNotNull(tokenStatusStr, "token:{tokenValue} 해시에서 token_status를 찾을 수 없음");

                    TokenStatus tokenStatus = TokenStatus.valueOf(tokenStatusStr);
                    assertEquals(TokenStatus.ACTIVE, tokenStatus, "토큰 상태가 ACTIVE여야 함");

                    String issuedAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "issued_at");
                    String expiredAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "expired_at");

                    assertNotNull(issuedAtMillisStr, "issued_at이 존재해야 함");
                    assertNotNull(expiredAtMillisStr, "expired_at이 존재해야 함");

                    LocalDateTime issuedAt = Instant.ofEpochMilli(Long.parseLong(issuedAtMillisStr))
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    LocalDateTime expiredAt = Instant.ofEpochMilli(Long.parseLong(expiredAtMillisStr))
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    Duration duration = Duration.between(issuedAt, expiredAt);
                    assertEquals(5, duration.toMinutes(), "발급 시간과 만료 시간 차이는 5분이어야 함");
                });
    }

    @DisplayName("유저 정보 체크 후 만료 상태의 토큰이 있는 경우 해당 토큰 갱신 처리한다.")
    @Test
    void issueNewQueueExistingExpiredToken() {

        QueueRequest queueRequest = queueTransactionHelper.setupTestDataExistingExpiredQueue();

        //given
        Long userId = queueRequest.getUserId();

        //when
        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
        LocalDateTime now = LocalDateTime.now();

        // then: Consumer가 처리한 이후 Redis 상태 검증
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    // 1. user:{userId} 키에서 token_value 조회
                    String tokenValue = redisTemplate.opsForValue().get("user:" + userId + ":token");
                    assertNotNull(tokenValue, "user:{userId} 키에서 토큰 값을 찾을 수 없음");

                    // 2. token:{tokenValue} 해시에서 token_status, issued_at, expired_at 조회
                    String tokenStatusStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "token_status");
                    assertNotNull(tokenStatusStr, "token:{tokenValue} 해시에서 token_status를 찾을 수 없음");

                    TokenStatus tokenStatus = TokenStatus.valueOf(tokenStatusStr);
                    assertEquals(TokenStatus.ACTIVE, tokenStatus, "토큰 상태가 ACTIVE여야 함");

                    String issuedAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "issued_at");
                    String expiredAtMillisStr = (String) redisTemplate.opsForHash().get("token:" + tokenValue, "expired_at");

                    assertNotNull(issuedAtMillisStr, "issued_at이 존재해야 함");
                    assertNotNull(expiredAtMillisStr, "expired_at이 존재해야 함");

                    LocalDateTime issuedAt = Instant.ofEpochMilli(Long.parseLong(issuedAtMillisStr))
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    LocalDateTime expiredAt = Instant.ofEpochMilli(Long.parseLong(expiredAtMillisStr))
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();

                    Duration duration = Duration.between(issuedAt, expiredAt);
                    assertEquals(5, duration.toMinutes(), "발급 시간과 만료 시간 차이는 5분이어야 함");
                });
    }
}
