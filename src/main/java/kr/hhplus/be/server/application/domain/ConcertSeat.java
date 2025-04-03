package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "콘서트 좌석 정보")
public class ConcertSeat {

    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

//    @Schema(description = "좌석 번호 목록", example = "[\"1\", \"2\", \"3\", \"4\"]")
//    private List<String> seatNum;
//
//    @Schema(description = "좌석 점유 상태", example = "")
//    private SeatStatus seatStatus;

    @Schema(description = "좌석 상태 목록", example = "{ \"1\": \"AVAILABLE\", \"2\": \"RESERVED\", \"3\": \"SOLD\" }")
    private Map<String, SeatStatus> seatStatusMap;


    public ConcertSeat() {
    }

    public ConcertSeat(Long concertId, Map<String, SeatStatus> seatStatusMap) {
        this.concertId = concertId;
        this.seatStatusMap = seatStatusMap;
    }

    public Map<String, SeatStatus> getSeatStatusMap() {
        return seatStatusMap;
    }

    public void setSeatStatusMap(Map<String, SeatStatus> seatStatusMap) {
        this.seatStatusMap = seatStatusMap;
    }

    public void setSeatStatus(String seatNum, SeatStatus status) {
        seatStatusMap.put(seatNum, status);
    }

    public SeatStatus getSeatStatus() {
        return seatStatusMap.get(concertId.toString());
    }
}