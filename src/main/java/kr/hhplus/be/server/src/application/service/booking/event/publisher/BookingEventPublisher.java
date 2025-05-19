package kr.hhplus.be.server.src.application.service.booking.event.publisher;

import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(SeatBookedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(ConcertBookingScoreIncrementEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(ExternalDataSaveEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

//    public <T extends ApplicationEvent> void success(T event) {
//        applicationEventPublisher.publishEvent(event);
//    }
}
