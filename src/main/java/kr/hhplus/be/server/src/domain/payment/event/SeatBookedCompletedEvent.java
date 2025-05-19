package kr.hhplus.be.server.src.domain.payment.event;

import kr.hhplus.be.server.src.domain.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatBookedCompletedEvent {

    private Seat seat;
}
