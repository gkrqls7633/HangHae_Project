package kr.hhplus.be.server.src.domain.model;


import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
}
