package kr.hhplus.be.server.application.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.domain.PaymentStatus;

@Schema(description = "결제 처리 응답")
public class PaymentResponse {

    public PaymentResponse() {

    }

    @Schema(description = "결제Id", example = "1")
    @JsonProperty("paymentId")
    private Long paymentId;

    @Schema(description = "예약Id", example = "1")
    @JsonProperty("bookingId")
    private String bookingId;

    @Schema(description = "결제 상태", example = "COMPLETED")
    @JsonProperty("paymentStatus")
    private PaymentStatus paymentStatus;

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

}


