package kr.hhplus.be.server.src.domain.queue;


import kr.hhplus.be.server.src.domain.enums.TokenStatus;
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
        queue = queue.newToken(123L);
    }

    @DisplayName("신규 토큰 발급한다.")
    @Test
    void newTokenTest() {

        Queue mockQueue = queue;

        // then
        // 1. tokenValue는 UUID 형식의 값이어야 한다.
        assertNotNull(mockQueue.getTokenValue());
        assertTrue(UUID.fromString(mockQueue.getTokenValue()) instanceof UUID);

        // 2. tokenStatus는 대기중(ready) 상태다.
        assertEquals(TokenStatus.READY, mockQueue.getTokenStatus());

        // 3. issuedAt은 현재 시간보다 바로 직전이다.
        assertNotNull(mockQueue.getIssuedAt());
        assertTrue(mockQueue.getIssuedAt().isBefore(LocalDateTime.now()));

        // 4. expiredAt은 issuedAt + TOKEN_EXPIRE_MINUTES 후로 셋팅된다.
        assertNotNull(mockQueue.getExpiredAt());
        assertTrue(mockQueue.getExpiredAt().isAfter(mockQueue.getIssuedAt()));
        Duration duration = Duration.between(mockQueue.getIssuedAt(), mockQueue.getExpiredAt());

        //5분 차이 나야한다.
        assertTrue("expiredAt should be 5 minutes after issuedAt, but difference is " + duration.toMinutes() + " minutes.",
                duration.toMinutes() == 5);

    }

    @Test
    @DisplayName("토큰 대기상태 유저의 토큰을 갱신한다.")
    void refreshReadyTokenTest() {

        //given
        String asisTokenValue = queue.getTokenValue();
        queue.setTokenStatus(TokenStatus.READY);
        queue.setIssuedAt(LocalDateTime.now().minusMinutes(10));
        queue.setExpiredAt(queue.getIssuedAt().plusMinutes(5));

        //when
        queue.refreshToken();
        String tobeTokenValue = queue.getTokenValue();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertEquals(asisTokenValue, tobeTokenValue);
        assertThat(queue.getTokenStatus()).isEqualTo(TokenStatus.READY);
        assertThat(queue.getIssuedAt().isAfter(now.minusSeconds(2)));
        assertThat(queue.getExpiredAt().isEqual(queue.getIssuedAt()));

    }

    @Test
    @DisplayName("토큰 만료상태 유저의 토큰은 갱신하지 않는다.")
    void refreshExpiredTokenTest() {

        //given
        String asisTokenValue = queue.getTokenValue();
        queue.setTokenStatus(TokenStatus.EXPIRED);
        queue.setIssuedAt(LocalDateTime.now().minusMinutes(10));
        queue.setExpiredAt(queue.getIssuedAt().plusMinutes(5));

        //when
        queue.refreshToken();
        String tobeTokenValue = queue.getTokenValue();

        //then
        LocalDateTime now = LocalDateTime.now();
        assertEquals(asisTokenValue, tobeTokenValue);
        assertThat(queue.getTokenStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(queue.getIssuedAt().isAfter(now.minusSeconds(0)));


    }
}
