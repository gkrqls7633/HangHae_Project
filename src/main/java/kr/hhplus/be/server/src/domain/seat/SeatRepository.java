package kr.hhplus.be.server.src.domain.seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findByConcertSeat_Concert_ConcertIdAndSeatNum(Long concertId, Long seatNum);

    Seat save(Seat seat);

    Optional<Seat> findById(Long seatId);

    void saveAll(List<Seat> seatList);

    void deleteAll();

    void deleteAllInBatch();
}
