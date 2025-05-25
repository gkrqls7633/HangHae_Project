package kr.hhplus.be.server.src.infra.events;

import kr.hhplus.be.server.src.domain.booking.event.BookingFailureEventPublisher;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingFailureEventPublisherImpl implements BookingFailureEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void fail(ConcertBookingScoreIncrementFailedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
