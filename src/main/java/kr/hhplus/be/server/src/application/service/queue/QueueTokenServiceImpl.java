package kr.hhplus.be.server.src.application.service.queue;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.QueueTokenService;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueTokenServiceImpl implements QueueTokenService {

    private final QueueRepository queueRepository;
    private final UserRepository userRepository;
    private final RedisQueueRepository redisQueueRepository;

    @Override
    @Transactional
    public void processTokenIssue(Long userId, Long concertId) {

        // 1. User 정보를 조회해서 해당 유저에 대한 정보를 가져옵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        // 2. 해당 유저가 이미 발급 받은 ACTIVE / EXPIRED 상태의 토큰이 있는지 조회
        Optional<Queue> existingActiveQueue = redisQueueRepository.findByUserIdAndTokenStatus(userId, TokenStatus.ACTIVE);
        Optional<Queue> existingExpiredQueue = redisQueueRepository.findByUserIdAndTokenStatus(userId, TokenStatus.EXPIRED);

        //활성화 중인 토큰 존재하는 경우 갱신 / 만료상태 토큰이 있거나 or 토큰 존재하지 않는 경우 신규 발급
        if (existingActiveQueue.isPresent()) {
            Queue activeQueue = existingActiveQueue.get();

            //토큰 갱신 처리
            activeQueue.refreshToken();
            redisQueueRepository.save(activeQueue);
            log.info("[토큰 갱신] userId={}, token={}", userId, activeQueue.getTokenValue());

        } else {

            // 해당 유저가 기존에 만료된 토큰을 갖고 있을 경우
            if (existingExpiredQueue.isPresent()) {
                Queue expiredQueue = existingExpiredQueue.get();

                //만료된 토큰은 제거하고 신규 토큰 발행.
                Queue queue = Queue.newToken(user.getUserId());
                redisQueueRepository.removeExpiredQueue(expiredQueue);
                redisQueueRepository.save(queue);
                log.info("[만료 토큰 제거 및 신규 토큰 발급] userId={}, token={}", userId, queue.getTokenValue());

                // 최초로 신규 토큰 발급하는 경우
            } else{
                //토큰 신규 발급 처리
                Queue queue = Queue.newToken(user.getUserId());
                redisQueueRepository.save(queue);
                log.info("[토큰 신규 발급] userId={}, token={}", userId, queue.getTokenValue());
            }

            //todo : 토큰 활성화 'queue-promote' 토픽 발행

        }
    }

}
