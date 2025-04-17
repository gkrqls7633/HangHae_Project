package kr.hhplus.be.server.src.domain.model;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import lombok.*;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Schema(description = "결제 도메인")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "결제id", example = "1")
    private Long paymentId;

    @Schema(description = "예약 ID", example = "1")
    private Long bookingId;

    @Schema(description = "유저id", example = "1")
    private Long userId;

    @Schema(description = "결제 상태", example = "COMPLETED")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public boolean isBookingCheck(Booking booking) {
        return booking != null && booking.isBookedBy(this.userId);
    }

}
