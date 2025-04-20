package kr.hhplus.be.server.src.domain.concertseat;

import kr.hhplus.be.server.src.domain.seat.Seat;

import java.util.List;

public interface ConcertSeatRepository {
    List<Seat> findAllSeatsByConcertId(Long concertId);

    ConcertSeat save(ConcertSeat concertSeat);
}
