package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.infrastructure.repository.ConcertSeatRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertSeatRepository extends JpaRepository<ConcertSeat, Long>, ConcertSeatRepositoryCustom {
}
