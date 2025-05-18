package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueResponse;
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

    /**
     * @param queueRequest
     * @return
     * @description 특정 유저의 활성화 상태의 토큰이 존재 하는지 확인 후 없는 경우 '신규 토큰 발급', 있는 경우 '토큰 갱신'
     */
    @Override
    @Transactional
    public ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest) {

        // 1. User 정보를 조회해서 해당 유저에 대한 정보를 가져옵니다.
        User user = userRepository.findById(queueRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        // 2. 해당 유저가 이미 발급 받은 ACTIVE / EXPIRED 상태의 토큰이 있는지 조회
        Optional<Queue> existingActiveQueue = redisQueueRepository.findByUserIdAndTokenStatus(queueRequest.getUserId(), TokenStatus.ACTIVE);
        Optional<Queue> existingExpiredQueue = redisQueueRepository.findByUserIdAndTokenStatus(queueRequest.getUserId(), TokenStatus.EXPIRED);

        QueueResponse queueResponse;

        //활성화 중인 토큰 존재하는 경우 갱신 / 만료상태 토큰이 있거나 or 토큰 존재하지 않는 경우 신규 발급
        if (existingActiveQueue.isPresent()) {
            Queue activeQueue = existingActiveQueue.get();

            //토큰 갱신 처리
            activeQueue.refreshToken();
            redisQueueRepository.save(activeQueue);

            queueResponse = QueueResponse.builder()
                    .tokenValue(activeQueue.getTokenValue())
                    .tokenStatus(activeQueue.getTokenStatus())
                    .issuedAt(activeQueue.getIssuedAt())
                    .expiredAt(activeQueue.getExpiredAt())
                    .build();
            return ResponseMessage.success("대기열 토큰을 갱신 완료했습니다.", queueResponse);

        } else {

            // 해당 유저가 기존에 만료된 토큰을 갖고 있을 경우
            if (existingExpiredQueue.isPresent()) {
                Queue expiredQueue = existingExpiredQueue.get();

                //만료된 토큰은 제거하고 신규 토큰 발행.
                Queue queue = Queue.newToken(user.getUserId());
                redisQueueRepository.removeExpiredQueue(expiredQueue);
                redisQueueRepository.save(queue);

                queueResponse = QueueResponse.builder()
                        .tokenValue(queue.getTokenValue())
                        .tokenStatus(queue.getTokenStatus())
                        .issuedAt(queue.getIssuedAt())
                        .expiredAt(queue.getExpiredAt())
                        .build();
                return ResponseMessage.success("대기열 토큰을 갱신 완료했습니다.", queueResponse);

            // 최초로 신규 토큰 발급하는 경우
            } else{
                //토큰 신규 발급 처리
                Queue queue = Queue.newToken(user.getUserId());
                redisQueueRepository.save(queue);

                queueResponse = QueueResponse.builder()
                        .tokenValue(queue.getTokenValue())
                        .tokenStatus(queue.getTokenStatus())
                        .issuedAt(queue.getIssuedAt())
                        .expiredAt(queue.getExpiredAt())
                        .build();
                return ResponseMessage.success("대기열 토큰을 발급 완료했습니다.", queueResponse);
            }
        }
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
