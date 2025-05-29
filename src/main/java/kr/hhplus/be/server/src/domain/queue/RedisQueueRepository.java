package kr.hhplus.be.server.src.domain.queue;

import kr.hhplus.be.server.src.domain.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RedisQueueRepository {

    Optional<Queue> findByUserIdAndTokenStatus(Long userId, TokenStatus tokenStatus);

    Queue save(Queue queue);

    Optional<Queue> findByTokenValueAndTokenStatus(String tokenValue, TokenStatus tokenStatus);

    List<Queue> findByExpiredAtBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus);

    List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime now);

    Queue findByTokenStatus(TokenStatus tokenStatus);

    void removeExpiredQueue(Queue expiredQueue);

    String getUserTokenValue(Long userId);

    Set<String> getReadyTokens(String tokenValue, LocalDateTime now);

    String getTokenStatus(Long userId, String tokenValue);

}
