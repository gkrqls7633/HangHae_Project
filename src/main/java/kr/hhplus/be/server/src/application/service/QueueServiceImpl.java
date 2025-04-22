package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    /**
     * @param queueRequest
     * @return
     * @description 특정 유저의 활성화 상태의 토큰이 존재하는지 확인 후 없는 경우 '신규 토큰 발급', 있는 경우 '토큰 갱신'
     */
    @Override
    @Transactional
    public ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest) {

        // 1. User 정보를 조회해서 해당 유저에 대한 정보를 가져옵니다.
        User user = userRepository.findById(queueRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        // 2. 해당 유저가 이미 발급 받은 ACTIVE 상태의 토큰이 있는지 조회
        Optional<Queue> existingQueue = queueRepository.findByUserIdAndTokenStatus(queueRequest.getUserId(), TokenStatus.ACTIVE);

        QueueResponse queueResponse;

        //토큰 존재하는 경우 갱신 / 존재하지 않는 경우 신규 발급
        if (existingQueue.isPresent()) {
            Queue activeQueue = existingQueue.get();

            //토큰 갱신 처리
            activeQueue.refreshToken();
            queueRepository.save(activeQueue);

            queueResponse = QueueResponse.builder()
                    .tokenValue(activeQueue.getTokenValue())
                    .tokenStatus(activeQueue.getTokenStatus())
                    .issuedAt(activeQueue.getIssuedAt())
                    .expiredAt(activeQueue.getExpiredAt())
                    .build();
        } else {
            //토큰 신규 발급 처리
            Queue queue = Queue.newToken();
            queueRepository.save(queue);

            queueResponse = QueueResponse.builder()
                    .tokenValue(queue.getTokenValue())
                    .tokenStatus(queue.getTokenStatus())
                    .issuedAt(queue.getIssuedAt())
                    .expiredAt(queue.getExpiredAt())
                    .build();
        }

        return ResponseMessage.success("대기열 토큰을 발급 완료했습니다.", queueResponse);
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
        Queue queue = queueRepository.findById(queueExpireRequest.getQueueId())
                .orElseThrow(() -> new EntityNotFoundException("해당 토큰이 존재하지 않습니다."));

        // 2. 토큰 만료 처리
        if (queue.isExpired()) {
            queue.setTokenStatus(TokenStatus.EXPIRED);
            queueRepository.save(queue);
        }

        QueueResponse queueResponse = QueueResponse.builder()
                .tokenValue(queue.getTokenValue())
                .tokenStatus(queue.getTokenStatus())
                .issuedAt(queue.getIssuedAt())
                .expiredAt(queue.getExpiredAt())
                .build();

        return ResponseMessage.success("대기열 토큰이 만료되었습니다.", queueResponse);
    }

}
