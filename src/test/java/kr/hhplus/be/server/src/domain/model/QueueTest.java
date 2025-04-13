package kr.hhplus.be.server.src.domain.model;


import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

class QueueTest {

    private Queue queue;

    @BeforeEach
    void setUp() {
        // Queue 객체 초기화
        queue = new Queue();
    }

    @DisplayName("신규 토큰 발급 시 유효한 토큰이 존재하면 실패한다.")
    @Test
    void newTokenValidateTest() {
        //given
        queue.setTokenStatus(TokenStatus.ACTIVE);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            queue.validateActiveToken();
        });

        assertEquals("이미 유효한 토큰이 존재합니다.", exception.getMessage());
    }

    @DisplayName("신규 토큰 발급한다.")
    @Test
    void newTokenTest() {

        //when
        queue.newToken();

        // then
        // 1. tokenValue는 UUID 형식의 값이어야 한다.
        assertNotNull(queue.getTokenValue());
        assertTrue(UUID.fromString(queue.getTokenValue()) instanceof UUID);

        // 2. tokenStatus는 활성화 상태다.
        assertEquals(TokenStatus.ACTIVE, queue.getTokenStatus());

        // 3. issuedAt은 현재 시간보다 바로 직전이다.
        assertNotNull(queue.getIssuedAt());
        assertTrue(queue.getIssuedAt().isBefore(LocalDateTime.now()));

        // 4. expiredAt은 issuedAt + TOKEN_EXPIRE_MINUTES 후로 셋팅된다.
        assertNotNull(queue.getExpiredAt());
        assertTrue(queue.getExpiredAt().isAfter(queue.getIssuedAt()));
        Duration duration = Duration.between(queue.getIssuedAt(), queue.getExpiredAt());

        //5분 차이 나야한다.
        assertTrue("expiredAt should be 5 minutes after issuedAt, but difference is " + duration.toMinutes() + " minutes.",
                duration.toMinutes() == 5);

    }
}
