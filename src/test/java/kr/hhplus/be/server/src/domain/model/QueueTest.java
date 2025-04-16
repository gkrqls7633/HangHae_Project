package kr.hhplus.be.server.src.domain.model;


import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


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

    @Test
    @DisplayName("유저의 토큰을 갱신한다.")
    void refreshTokenTest() {

        //given
        queue.setTokenStatus(TokenStatus.ACTIVE);
        queue.setIssuedAt(LocalDateTime.now().minusMinutes(10));
        queue.setExpiredAt(queue.getIssuedAt().plusMinutes(5));

        //when
        queue.refreshToken();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertThat(queue.getIssuedAt().isAfter(now.minusSeconds(2)));
        assertThat(queue.getExpiredAt().isEqual(queue.getIssuedAt()));


    }
}
