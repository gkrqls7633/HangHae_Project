package kr.hhplus.be.server.src.infra.persistence.seat;

import kr.hhplus.be.server.src.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
}
