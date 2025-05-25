package kr.hhplus.be.server.src.interfaces.events;

import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertBookingScoreFailEventListener {

    @TransactionalEventListener
    @Async
    public void handle(ConcertBookingScoreIncrementFailedEvent event) {
        try {
            // 실패 이벤트의 예외 처리 (실제 실패 원인)
            log.error("콘서트 예약 점수 증가 실패 이벤트 수신 - concertId: {}, 에러 메시지: {}",
                    event.getConcertId(), event.getErrorMessage());

            // 실패 알림
            sendFailureNotification(event);

            // 실패 DB save
            mockSaveFailureLog(event);

        } catch (Exception e) {
            log.error("실패 이벤트 처리 중 오류 발생 - concertId: {}", event.getConcertId(), e);
        }
    }

    // Mock : 실패 알림
    private void sendFailureNotification(ConcertBookingScoreIncrementFailedEvent event) {
        log.warn("Mock 실패 알림 발송: 콘서트 예약 점수 증가 실패 - concertId: {}, 에러 메시지: {}",
                event.getConcertId(), event.getErrorMessage());
    }

    // Mock : 실패 DB save
    private void mockSaveFailureLog(ConcertBookingScoreIncrementFailedEvent event) {
        log.info("Mock: 실패 로그 저장 - concertId: {}, 에러 메시지: {}",
                event.getConcertId(), event.getErrorMessage());
    }
}
