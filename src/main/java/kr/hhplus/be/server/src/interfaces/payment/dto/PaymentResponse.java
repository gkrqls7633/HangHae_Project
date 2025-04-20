package kr.hhplus.be.server.src.interfaces.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "결제 처리 응답")
public class PaymentResponse {

    @Schema(description = "결제Id", example = "1")
    @JsonProperty("paymentId")
    private Long paymentId;

    @Schema(description = "예약Id", example = "1")
    @JsonProperty("bookingId")
    private Long bookingId;

    @Schema(description = "결제 상태", example = "COMPLETED")
    @JsonProperty("paymentStatus")
    private PaymentStatus paymentStatus;


}


