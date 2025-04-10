package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
