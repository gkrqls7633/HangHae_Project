package kr.hhplus.be.server.src.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.payment.PaymentService;
import kr.hhplus.be.server.src.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.api.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제", description = "결제 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 요청", description = "예약된(점유상태) 좌석의 결제를 요청한다.")
    @PostMapping("")
    public ResponseMessage<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {

        if(paymentRequest.getBookingId() == null || paymentRequest.getBookingId().longValue() == 0L) {
            return ResponseMessage.error(400, "결제에 실패했습니다.");
        }

        return paymentService.processPayment(paymentRequest);
    }
}
