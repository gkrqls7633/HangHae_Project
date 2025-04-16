package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Schema(description = "예약 도메인")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "얘약 번호", example = "1")
    private Long bookingId;

    // 결제와 1:1 관계
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "booking") // casecade.all은 부모가 자식을 자동으로 persist,remove함
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_concert_id")
    @Schema(description = "콘서트 정보",
            example = "{ \"concertId\": 1, \"name\": \"BTS World Tour\", \"price\": 150000, \"date\": \"2025-05-01\", \"time\": \"19:00\", \"location\": \"서울 올림픽 경기장\" }")
    private Concert concert;

    @Schema(description = "좌석 Id", example = "1")
    private Long seatId;

    @Schema(description = "좌석 번호", example = "1")
    private Long seatNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Booking(Concert concert, Long seatNum, User user) {
        this.concert = concert;
        this.seatNum = seatNum;
        this.user = user;
    }

    // 좌석 예약 가능 여부 체크
    // 객체 조회 흐름 : concert -> concertSeat -> seats -> seatStatus
    public boolean isAvailableBooking() {
        ConcertSeat concertSeat = concert.getConcertSeat();
        List<Seat> seatList = concertSeat.getSeats();
        Seat filteredSeat = seatList.stream()
                .filter(seat -> this.seatNum.equals(seat.getSeatNum()))
                .findFirst()
                .orElse(null);

        return "AVAILABLE".equals(filteredSeat.getSeatStatus().getCode());
    }

}
