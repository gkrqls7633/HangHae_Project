package kr.hhplus.be.server.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Payment;
import kr.hhplus.be.server.application.domain.PaymentStatus;
import kr.hhplus.be.server.application.response.PaymentResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관리 API")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Operation(summary = "결제 요청", description = "결제를 요청한다.")
    @PostMapping("")
    public ResponseMessage<PaymentResponse> payment(@RequestBody Payment payment) {

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentResponse.setBookingId(payment.getBookingId());

        return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);
    }
}
