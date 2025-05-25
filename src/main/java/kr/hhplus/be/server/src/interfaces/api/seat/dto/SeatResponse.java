package kr.hhplus.be.server.src.interfaces.api.seat.dto;

import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.seat.Seat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {

    private Long seatNum;
    private SeatStatus seatStatus;

    public static SeatResponse from(Seat seat) {
        return SeatResponse.builder()
                .seatNum(seat.getSeatNum())
                .seatStatus(seat.getSeatStatus())
                .build();
    }
}
