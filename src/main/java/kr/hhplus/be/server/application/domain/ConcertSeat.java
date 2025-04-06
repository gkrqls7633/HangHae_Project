package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "콘서트 좌석 정보")
public class ConcertSeat {

    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "좌석 상태 목록", example = "{ \"1\": \"AVAILABLE\", \"2\": \"RESERVED\", \"3\": \"SOLD\" }")
    private Map<String, SeatStatus> seatStatusMap;


    public SeatStatus getSeatStatus(String seatNum) {
        return seatStatusMap.get(seatNum);
    }
}