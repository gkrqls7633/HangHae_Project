package kr.hhplus.be.server.src.infra.concertseat;

import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long> {

    /*
    SELECT s.*
    FROM seat s
    JOIN concert_seat cs ON s.concert_seat_id = cs.concert_seat_id
    JOIN concert c ON cs.concert_id = c.concert_id
    WHERE c.concert_id = ?
     */
    @Query("SELECT s FROM Seat s " +
            "JOIN s.concertSeat cs " +
            "JOIN cs.concert c " +
            "WHERE c.concertId = :concertId")
    List<Seat> findAllSeatsByConcertId(Long concertId);
}
