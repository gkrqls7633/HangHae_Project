package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.infrastructure.repository.ConcertSeatRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long>, ConcertSeatRepositoryCustom {

    /*
    SELECT s.*
    FROM seat s
    JOIN concert_seat cs ON s.concert_seat_id = cs.concert_seat_id
    JOIN concert c ON cs.concert_id = c.concert_id
    WHERE c.concert_id = ?
     */
    @Query("SELECT cs FROM ConcertSeat cs JOIN FETCH cs.seats WHERE cs.concert.concertId = :concertId")
    List<Seat> findAllSeatsByConcertId(Long concertId);
}
