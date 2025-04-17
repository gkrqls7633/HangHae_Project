package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.infrastructure.repository.QueueRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long>, QueueRepositoryCustom {

    /* 유저 ID와 상태로 토큰 조회 */
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.tokenStatus = :tokenStatus")
    Optional<Queue> findByUserIdAndTokenStatus(@Param("userId") Long userId, @Param("tokenStatus") TokenStatus tokenStatus);

    List<Queue> findByTokenStatus(TokenStatus tokenStatus);

    List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime currentTime);
}
