package kr.hhplus.be.server.src.domain.booking.event;

import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;

public interface BookingEventPublisher {

    void success(SeatBookedEvent event);

    void success(ConcertBookingScoreIncrementEvent event);

    void success(ExternalDataSaveEvent event);

}
