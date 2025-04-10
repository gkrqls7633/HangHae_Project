package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.infrastructure.repository.ConcertRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> , ConcertRepositoryCustom {

}
