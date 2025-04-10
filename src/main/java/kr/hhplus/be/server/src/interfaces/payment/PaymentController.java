package kr.hhplus.be.server.src.interfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Payment;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.service.PaymentService;
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

        // todo : parameter 체크 PaymentRequest DTO로 옮기자
        if(paymentRequest.getBookingId() == null || paymentRequest.getBookingId().longValue() == 0L) {
            return ResponseMessage.error(400, "결제에 실패했습니다.");
        }

        return paymentService.processPayment(paymentRequest);
    }
}
