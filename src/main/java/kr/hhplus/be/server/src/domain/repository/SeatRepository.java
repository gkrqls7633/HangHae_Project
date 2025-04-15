package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.infrastructure.repository.SeatRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long>, SeatRepositoryCustom {
}
