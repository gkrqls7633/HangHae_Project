package kr.hhplus.be.server.src.application.service.queue;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.queue.event.QueueEventPublisher;
import kr.hhplus.be.server.src.domain.queue.event.QueueTokenIssuedEvent;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final RedisQueueRepository redisQueueRepository;

    private final QueueEventPublisher queueEventPublisher;

    /**
     * @param queueRequest
     * @return
     * @description 특정 유저의 활성화 상태의 토큰이 존재 하는지 확인 후 없는 경우 '신규 토큰 발급', 있는 경우 '토큰 갱신'
     */
    @Override
    @Transactional
    public ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest) {

        //kafa 'user-token-issued' 토픽 이벤트 발행
        queueEventPublisher.success(new QueueTokenIssuedEvent(queueRequest.getUserId(), queueRequest.getConcertId()));

        QueueResponse response = QueueResponse.builder()
                .tokenStatus(TokenStatus.READY)
                .build();

        return ResponseMessage.success("대기열 요청이 정상적으로 처리되었습니다.", response);
    }


    /**
     * @param queueExpireRequest
     * @return
     * @description controller에서 호출되는 단건으로 토큰 만료시키는 메서드 / 스케줄러에서 토큰 만료 호출하는 메서드
     */
    @Override
    @Transactional
    public ResponseMessage<QueueResponse> expireQueueToken(QueueExpireRequest queueExpireRequest) {

        //1. 만료 요청 들어온 토큰 조회
        Queue queue = redisQueueRepository.findByTokenValueAndTokenStatus(
                queueExpireRequest.getTokenValue(),
                TokenStatus.ACTIVE
        ).orElseThrow(() -> new EntityNotFoundException("해당 토큰이 존재하지 않거나, 이미 만료된 상태입니다."));

        // 2. 토큰 만료 처리
        if (queue.isExpired()) {
            queue.setTokenStatus(TokenStatus.EXPIRED);

            //active -> expired 상태 변경 (할 필요 있나..?)
            //redisQueueRepository.save(queue);

            // 그냥 다 지워버리자.
            redisQueueRepository.removeExpiredQueue(queue);

            QueueResponse queueResponse = QueueResponse.builder()
                    .tokenValue(queue.getTokenValue())
                    .tokenStatus(queue.getTokenStatus())
                    .issuedAt(queue.getIssuedAt())
                    .expiredAt(queue.getExpiredAt())
                    .build();

            return ResponseMessage.success("대기열 토큰이 만료되었습니다.", queueResponse);

        } else {
            QueueResponse queueResponse = QueueResponse.builder()
                    .tokenValue(queue.getTokenValue())
                    .tokenStatus(queue.getTokenStatus())
                    .issuedAt(queue.getIssuedAt())
                    .expiredAt(queue.getExpiredAt())
                    .build();

            return ResponseMessage.success("대기열 토큰은 아직 유효합니다.", queueResponse);
        }
    }

    /*
     만료된 토큰들 조회
     */
    @Override
    public List<Queue> findExpiredQueues(LocalDateTime now, TokenStatus tokenStatus) {
        List<Queue> expiredQueues = redisQueueRepository.findByExpiredAtBeforeAndTokenStatus(now, tokenStatus);

        return expiredQueues;
    }

    /*
    READY 상태 & 만료 되지 않은 토큰들 조회
     */
    @Override
    public List<Queue> findReadToActivateTokens(TokenStatus tokenStatus, LocalDateTime now) {
        List<Queue> readyQueues = redisQueueRepository.findByTokenStatusAndExpiredAtAfter(
                  TokenStatus.READY
                , LocalDateTime.now()
        );
        return readyQueues;
    }

    @Override
    public Queue save(Queue queue) {
        return redisQueueRepository.save(queue);
    }
}
