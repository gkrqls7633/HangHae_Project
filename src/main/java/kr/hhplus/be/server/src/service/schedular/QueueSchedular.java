package kr.hhplus.be.server.src.service.schedular;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.QueueRepository;
import kr.hhplus.be.server.src.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QueueSchedular {

    private final QueueRepository queueRepository;

    private final QueueService queueService;

    @Scheduled(cron = "0 0/1 * * * *")
    public void expireExpiredTokens() {

        // 1. 만료된 토큰들 조회
        LocalDateTime now = LocalDateTime.now();
        List<Queue> expiredQueues = queueRepository.findByExpiredAtBeforeAndTokenStatus(now, TokenStatus.ACTIVE);

        // 2. 만료된 토큰들 처리 위해 QueueService.expireQueueToken 호출
        for (Queue queue : expiredQueues) {
            queueService.expireQueueToken(queue);
        }

        System.out.println(expiredQueues.size() + "개의 토큰이 만료되었습니다.");
    }
}
