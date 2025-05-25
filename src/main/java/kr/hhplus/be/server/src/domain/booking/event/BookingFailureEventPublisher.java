package kr.hhplus.be.server.src.domain.booking.event;

public interface BookingFailureEventPublisher {

    void fail(ConcertBookingScoreIncrementFailedEvent event);
}
