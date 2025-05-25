package kr.hhplus.be.server.src.infra.persistence.concert;

import kr.hhplus.be.server.src.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByConcertStartDateBefore(LocalDateTime now);
}
