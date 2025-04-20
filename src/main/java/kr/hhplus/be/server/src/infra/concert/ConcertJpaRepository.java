package kr.hhplus.be.server.src.infra.concert;

import kr.hhplus.be.server.src.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
