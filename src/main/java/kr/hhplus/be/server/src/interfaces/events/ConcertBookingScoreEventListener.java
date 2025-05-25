package kr.hhplus.be.server.src.interfaces.events;

import kr.hhplus.be.server.src.domain.booking.event.BookingFailureEventPublisher;
import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConcertBookingScoreEventListener {

    private final BookingRankingRepository bookingRankingRepository;
    private final BookingFailureEventPublisher bookingFailureEventPublisher;

    @EventListener
    @Transactional
    @Async
    public void handle(ConcertBookingScoreIncrementEvent event) {
        try {
            bookingRankingRepository.incrementConcertBookingScore(event.getConcertId());
        } catch (Exception e) {
            log.error("콘서트 랭킹 점수 증가 실패 - concertId: {}", event.getConcertId(), e);

            //실패 이벤트 발행
            bookingFailureEventPublisher.fail(new ConcertBookingScoreIncrementFailedEvent(event.getConcertId(), e));
        }
    }

}
