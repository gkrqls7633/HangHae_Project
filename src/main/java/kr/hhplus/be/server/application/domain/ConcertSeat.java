package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "콘서트 좌석 정보")
public class ConcertSeat {

    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "좌석 번호 목록", example = "[\"1\", \"2\", \"3\", \"4\"]")
    private List<String> seatNum;

    @Schema(description = "좌석 점유 상태", example = "")
    private SeatStatus seatStatus;

    public ConcertSeat() {
    }

    public ConcertSeat(Long concertId, List<String> seatNum, SeatStatus seatStatus) {
        this.concertId = concertId;
        this.seatNum = seatNum;
        this.seatStatus = seatStatus;
    }

    public ConcertSeat(Long concertId, List<String> seatNum) {
        this.concertId = concertId;
        this.seatNum = seatNum;
    }

    public Long getConcertId() {
        return concertId;
    }

    public List<String> getSeatNum() {
        return seatNum;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }
}