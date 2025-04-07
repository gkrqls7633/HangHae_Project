package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Schema(description = "결제")
public class Payment {

    public Payment(String bookingId) {
        this.bookingId = bookingId;
    }

    @Id
    @Schema(description = "결제id", example = "1")
    private Long paymentId;

    @Schema(description = "예약id", example = "1")
    private String bookingId;

    public String getBookingId() {
        return bookingId;
    }

}
