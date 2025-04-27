package kr.hhplus.be.server.src.domain.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "payment", indexes = {
        @Index(name = "idx_booking_id", columnList = "booking_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Schema(description = "결제 도메인")
public class Payment extends BaseTimeEntity {

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

    public void changePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

}
