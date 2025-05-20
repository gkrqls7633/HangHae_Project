package kr.hhplus.be.server.src.domain.booking.event;

import kr.hhplus.be.server.src.domain.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatBookedEvent {

    private Seat seat;

}
