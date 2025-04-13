package kr.hhplus.be.server.src.domain.model;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
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

    @OneToOne(fetch = FetchType.LAZY) // 결제와 예약은 1:1 관계
    @JoinColumn(name = "bookingId", referencedColumnName = "bookingId", nullable = false)
    @Schema(description = "예약", example = "1")
    private Booking booking;


    @Schema(description = "유저id", example = "1")
    private Long userId;

    public boolean isBookingCheck() {

        // booking 객체와 booking.getUserId()가 null인 경우를 체크
        if (booking == null || booking.getUser().getUserId() == null) {
            return false;  // booking이 없거나 userId가 없으면 false 반환
        }

        // 결제 요청 userId와 예약된 userId 비교
        return Objects.equals(booking.getUser().getUserId(), this.userId);

    }




}
