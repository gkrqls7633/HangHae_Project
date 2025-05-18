package kr.hhplus.be.server.src.interfaces.scehdular;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import kr.hhplus.be.server.src.interfaces.queue.dto.QueueExpireRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class QueueSchedular {

    private final QueueService queueService;

    // 토큰 만료 시간 정책 : 10초 임시 셋팅
//    @Scheduled(cron = "0 0/1 * * * *")
    @Scheduled(cron = "*/10 * * * * *")//
    public void expireTokens() {

        // 1. 만료된 토큰들 조회
        LocalDateTime now = LocalDateTime.now();
        List<Queue> expiredQueues = queueService.findExpiredQueues(now, TokenStatus.ACTIVE);

        // 2. 만료된 토큰 없으면 바로 종료
        if (expiredQueues.isEmpty()) {
            log.info(" --- 만료된 토큰이 없습니다. ---");
            return;
        }

        // 3. 만료된 토큰들 처리 위해 QueueService.expireQueueToken 호출
        for (Queue queue : expiredQueues) {
            try {
                QueueExpireRequest expireRequest = new QueueExpireRequest(
                        queue.getQueueId(),
                        queue.getTokenValue(),
                        queue.getIssuedAt(),
                        queue.getExpiredAt(),
                        queue.getTokenStatus()
                );
                queueService.expireQueueToken(expireRequest);
                log.info("[토큰 만료 성공] tokenValue : {}, userId : {}", queue.getTokenValue(), queue.getUserId());
            } catch (EntityNotFoundException e) {
                log.warn("[토큰 만료 실패] tokenValue: {}, 이유: {}", queue.getTokenValue(), e.getMessage());
            } catch (Exception e) {
                log.error("[토큰 만료 중 예외 발생] tokenValue: {}", queue.getTokenValue(), e);
            }
        }
    }

    // 토큰 활성 시간 정책 : 10초 임시 셋팅
    @Scheduled(cron = "*/10 * * * * *")//
    public void readyToActivateTokens() {

        // 1. READY 상태 & 만료 되지 않은 토큰들 조회
        List<Queue> readyQueues = queueService.findReadToActivateTokens(TokenStatus.READY, LocalDateTime.now());

        if (readyQueues.isEmpty()) {
            log.info(" --- 활성화할 토큰이 없습니다. ---");
            return;
        }

        // 2. READY → ACTIVE 상태로 변경
        for (Queue queue : readyQueues) {
            queue.setTokenStatus(TokenStatus.ACTIVE);
            queue.refreshToken(); // 시간 갱신
            queueService.save(queue);
            log.info("[토큰 활성화 성공] tokenValue : {}, userId : {}", queue.getTokenValue(), queue.getUserId());
        }
        log.info(" {}개의 토큰이 ACTIVE 상태로 전환되었습니다.", readyQueues.size());

    }
}
