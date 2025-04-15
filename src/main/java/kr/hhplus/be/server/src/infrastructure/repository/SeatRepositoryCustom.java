package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Seat;

import java.util.Optional;

public interface SeatRepositoryCustom {

    //Seat → ConcertSeat → Concert
    Optional<Seat> findByConcertSeat_Concert_ConcertIdAndSeatNum(Long concertId, Long seatNum);

}
