package kr.hhplus.be.server.src.infra.booking.listener;

import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertBookingScoreEventListener {

    private final BookingRankingRepository bookingRankingRepository;

    @EventListener
    @Transactional
    @Async //비동기
    public void handle(ConcertBookingScoreIncrementEvent event) {

        bookingRankingRepository.incrementConcertBookingScore(event.getConcertId());
    }

}
