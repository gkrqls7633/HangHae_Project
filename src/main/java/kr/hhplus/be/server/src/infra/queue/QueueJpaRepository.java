package kr.hhplus.be.server.src.infra.queue;

import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {

//    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.tokenStatus = :tokenStatus")
    Optional<Queue> findByUserIdAndTokenStatus(Long userId, TokenStatus tokenStatus);

    List<Queue> findByTokenStatus(TokenStatus tokenStatus);

    List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime currentTime);
}
