package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.hhplus.be.server.src.common.enums.CommonEnumInterface;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Schema(description = "콘서트 좌석 정보")
public class Seat {

    @Id
    private Long seatId;

    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    public Seat(Long concertId, Map<String, SeatStatus> seatStatusMap) {
    }

    public CommonEnumInterface getSeatStatus(String seatNum) {
        return null;
    }

//    @Schema(description = "좌석 상태 목록", example = "{ \"1\": \"AVAILABLE\", \"2\": \"RESERVED\", \"3\": \"SOLD\" }")
//    private Map<String, SeatStatus> seatStatusMap;


//    public SeatStatus getSeatStatus(String seatNum) {
//        return seatStatusMap.get(seatNum);
//    }
}