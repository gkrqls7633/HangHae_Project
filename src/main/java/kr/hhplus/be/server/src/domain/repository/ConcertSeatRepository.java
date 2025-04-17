package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.infrastructure.repository.ConcertSeatRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long>, ConcertSeatRepositoryCustom {

    @Query("SELECT cs FROM ConcertSeat cs JOIN FETCH cs.seats WHERE cs.concert.concertId = :concertId")
    ConcertSeat findWithSeatsByConcertId(Long concertId);
}
