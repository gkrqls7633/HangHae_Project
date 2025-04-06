package kr.hhplus.be.server.src.interfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Payment;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관리 API")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Operation(summary = "결제 요청", description = "결제를 요청한다.")
    @PostMapping("")
    public ResponseMessage<PaymentResponse> payment(@RequestBody Payment payment) {

        PaymentResponse paymentResponse = new PaymentResponse();

        if(payment.getBookingId() == null || payment.getBookingId().isEmpty()) {
            return ResponseMessage.error(400, "결제에 실패했습니다.");
        }

        paymentResponse.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentResponse.setBookingId(payment.getBookingId());

        return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);
    }
}
