package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
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
@Schema(description = "예약")
public class Booking {

    @Id
    @Schema(description = "얘약 번호", example = "1")
    private Long bookingId;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)  // 결제와 1:1 관계
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "concert_concert_id")
    @Schema(description = "콘서트 정보",
        example = "{ \"concertId\": 1, \"name\": \"BTS World Tour\", \"price\": 150000, \"date\": \"2025-05-01\", \"time\": \"19:00\", \"location\": \"서울 올림픽 경기장\" }")
    private Concert concert;

    @Schema(description = "좌석 Id", example = "1")
    private Long seatId;

    @Schema(description = "좌석 번호", example = "1")
    private Long seatNum;

    //todo : userId 추가
    @Schema(description = "유저 Id", example = "1")
    private Long userId;

    //좌석 예약 가능 여부 체크
    public boolean isAvailableBooking() {

        // 객체 조회 흐름 : concert -> concertSeat -> seats -> seatStatus
        ConcertSeat concertSeat = concert.getConcertSeat();
        List<Seat> seatList = concertSeat.getSeats();
        Seat filteredSeat = seatList.stream()
                .filter(seat -> this.seatNum.equals(seat.getSeatNum()))
                .findFirst()
                .orElse(null);

        SeatStatus seatStatus = filteredSeat.getSeatStatus();

        if ("AVAILABLE".equals(seatStatus.getCode())) {
            return true;

        } else {
            return false;
        }
    }

}
