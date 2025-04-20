package kr.hhplus.be.server.src.domain.queue;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueResponse;


public interface QueueService {

    ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest);

    ResponseMessage<QueueResponse> expireQueueToken(QueueExpireRequest queueExpireRequest);

}
