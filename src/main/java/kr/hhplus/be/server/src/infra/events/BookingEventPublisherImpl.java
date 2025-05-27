package kr.hhplus.be.server.src.infra.events;

import kr.hhplus.be.server.src.domain.booking.event.BookingEventPublisher;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import kr.hhplus.be.server.src.domain.external.ExternalBookingDataSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

//@Component
@RequiredArgsConstructor
public class BookingEventPublisherImpl implements BookingEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(SeatBookedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(ConcertBookingScoreIncrementEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(ExternalBookingDataSaveEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

//    public <T extends ApplicationEvent> void success(T event) {
//        applicationEventPublisher.publishEvent(event);
//    }
}
