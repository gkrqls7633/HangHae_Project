package kr.hhplus.be.server.src.domain.queue;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface QueueService {

    ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest);

    ResponseMessage<QueueResponse> expireQueueToken(QueueExpireRequest queueExpireRequest);

    List<Queue> findExpiredQueues(LocalDateTime now, TokenStatus tokenStatus);

    List<Queue> findReadToActivateTokens(TokenStatus tokenStatus, LocalDateTime now);

    Queue save(Queue queue);
}
