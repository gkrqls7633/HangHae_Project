package kr.hhplus.be.server.src.domain.queue;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.queue.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueResponse;


public interface QueueService {

    ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest);

    ResponseMessage<QueueResponse> expireQueueToken(QueueExpireRequest queueExpireRequest);

}
