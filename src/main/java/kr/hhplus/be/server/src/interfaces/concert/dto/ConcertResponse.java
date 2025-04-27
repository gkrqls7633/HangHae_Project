package kr.hhplus.be.server.src.interfaces.concert.dto;

import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.interfaces.seat.dto.SeatResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConcertResponse {

    private Long concertId;
    private String name;
    private Long price;
    private String date;
    private String time;
    private String location;
    private ConcertSeat concertSeat;
    private List<SeatResponse> seatList;
}
