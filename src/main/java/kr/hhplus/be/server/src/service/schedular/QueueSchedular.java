package kr.hhplus.be.server.src.service.schedular;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.QueueRepository;
import kr.hhplus.be.server.src.interfaces.queue.QueueExpireRequest;
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

    //스케줄 시간 정책 : 5분 임시 셋팅
    @Scheduled(cron = "0 0/5 * * * *")
    public void expireTokens() {

        // 1. 만료된 토큰들 조회
        LocalDateTime now = LocalDateTime.now();
        List<Queue> expiredQueues = queueRepository.findByExpiredAtBeforeAndTokenStatus(now, TokenStatus.ACTIVE);

        // 2. 만료된 토큰 없으면 바로 종료
        if (expiredQueues.isEmpty()) {
            System.out.println("#####만료된 토큰이 없습니다.#####");
            return;
        }

        // 3. 만료된 토큰들 처리 위해 QueueService.expireQueueToken 호출
        for (Queue queue : expiredQueues) {
            QueueExpireRequest expireRequest = new QueueExpireRequest(
                    queue.getQueueId(),
                    queue.getTokenValue(),
                    queue.getIssuedAt(),
                    queue.getExpiredAt(),
                    queue.getTokenStatus()
            );
            queueService.expireQueueToken(expireRequest);
        }

        System.out.println(expiredQueues.size() + "개의 토큰이 만료되었습니다.");
    }

    // 토큰 활성 시간 정책 : 10초 임시 셋팅
    @Scheduled(cron = "*/10 * * * * *")//
    public void readyToActivateTokens() {

        // 1. READY 상태 & 만료 되지 않은 토큰들 조회
        List<Queue> readyQueues = queueRepository.findByTokenStatusAndExpiredAtAfter(
                  TokenStatus.READY
                , LocalDateTime.now()
        );

        if (readyQueues.isEmpty()) {
            System.out.println("#####활성화할 토큰이 없습니다.#####");
            return;
        }

        // 2. READY → ACTIVE 상태로 변경
        for (Queue queue : readyQueues) {
            queue.setTokenStatus(TokenStatus.ACTIVE);
            queue.refreshToken(); // 시간 갱신
            queueRepository.save(queue);
        }

        System.out.println(readyQueues.size() + "개의 토큰이 ACTIVE 상태로 전환되었습니다.");
    }
}
