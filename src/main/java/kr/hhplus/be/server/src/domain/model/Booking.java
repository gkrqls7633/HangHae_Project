package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "예약")
public class Booking {

    @Schema(description = "콘서트 정보",
        example = "{ \"concertId\": 1, \"name\": \"BTS World Tour\", \"price\": 150000, \"date\": \"2025-05-01\", \"time\": \"19:00\", \"location\": \"서울 올림픽 경기장\" }")
    private Concert concert;

    @Schema(description = "좌석 번호", example = "5")
    private String seatNum;

//    @Schema(description = "좌석")
//    private ConcertSeat concertSeat;

    //좌석 예약 가능 여부 체크
    public boolean isAvailableBooking(String seatNum) {

        if ("OCCUPIED".equals(concert.getSeat().getSeatStatus(seatNum).getCode())) {
            return false;
        }
        return true;
    }

}
