package kr.hhplus.be.server.src.domain.queue;

import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {

    Optional<Queue> findByUserIdAndTokenStatus(@Param("userId") Long userId, @Param("tokenStatus") TokenStatus tokenStatus);

    List<Queue> findAllByTokenStatus(TokenStatus tokenStatus);

    List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime currentTime);

    Queue save(Queue activeQueue);

    Optional<Queue> findById(Long queueId);

    List<Queue> findByExpiredAtBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus);

    Optional<Queue> findByTokenValueAndTokenStatus(String tokenValue, TokenStatus tokenStatus);

    Queue findByTokenStatus(TokenStatus tokenStatus);
}
