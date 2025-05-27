package kr.hhplus.be.server.src.domain.booking.event;

import kr.hhplus.be.server.src.domain.external.ExternalBookingDataSaveEvent;

public interface BookingEventPublisher {

    void success(SeatBookedEvent event);

    void success(ConcertBookingScoreIncrementEvent event);

    void success(ExternalBookingDataSaveEvent event);

}
