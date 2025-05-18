package kr.hhplus.be.server.src.domain.booking.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConcertBookingScoreIncrementEvent {

    private String concertId;
}
