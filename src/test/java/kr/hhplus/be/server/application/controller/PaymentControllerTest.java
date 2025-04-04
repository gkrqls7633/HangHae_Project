package kr.hhplus.be.server.application.controller;

import kr.hhplus.be.server.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Payment;
import kr.hhplus.be.server.application.domain.response.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PaymentControllerTest {

    @DisplayName("결제 요청 테스트")
    @Test
    void paymentTest(){

        //given
        PaymentController controller = new PaymentController();
        Payment payment = new Payment("1");

        // When
        ResponseMessage<PaymentResponse> response = controller.payment(payment);

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("결제가 완료됐습니다.", response.getMessage());

    }

    @DisplayName("payment 파라미터 중 bookId가 존재하지 않으면 결제할 수 없다.")
    @Test
    void paymentWithBookIdTest(){

        //given
        PaymentController controller = new PaymentController();
        Payment payment = new Payment("");

        // When
        ResponseMessage<PaymentResponse> response = controller.payment(payment);

        //then
        assertNotNull(response);
        assertNotEquals(200, response.getStatus());
        assertEquals("결제에 실패했습니다.", response.getMessage());

    }


}