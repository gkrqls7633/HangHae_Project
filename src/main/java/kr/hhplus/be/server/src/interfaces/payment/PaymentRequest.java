package kr.hhplus.be.server.src.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "결제 요청 Request DTO")
@Getter
public class PaymentRequest {

    @Schema(description = "예약 id", example = "1", required = true)
    private Long bookingId;

    @Schema(description = "유저 id", example = "123", required = true)
    private Long userId;

//    @Schema(description = "paymentPrice", example = "1", required = true)
//    private Long paymentPrice;

}
