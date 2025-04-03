package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "예약")
public class Booking {

    @Schema(description = "콘서트 정보",
        example = "{ \"concertId\": 1, \"name\": \"BTS World Tour\", \"price\": 150000, \"date\": \"2025-05-01\", \"time\": \"19:00\", \"location\": \"서울 올림픽 경기장\" }")
    private Concert concert;

    @Schema(description = "좌석 번호", example = "5")
    private String seatNum;

    @Schema(description = "좌석")
    private ConcertSeat concertSeat;

    public Booking(Concert concert, String seatNum) {
        this.concert = concert;
        this.seatNum = seatNum;
    }

    public Concert getConcert() {
        return concert;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public ConcertSeat getConcertSeat() {
        return concertSeat;
    }

    public void setConcertSeat(ConcertSeat concertSeat) {
        this.concertSeat = concertSeat;
    }


    //좌석 예약 가능 여부 체크
    public boolean isAvailableBooking() {
        if (Objects.equals(concertSeat.getSeatStatus().getCode(), "OCCUPIED")) {
            return false;
        }
        return true;
    }

}
