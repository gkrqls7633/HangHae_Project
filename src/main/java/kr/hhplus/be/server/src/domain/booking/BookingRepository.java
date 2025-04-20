package kr.hhplus.be.server.src.domain.booking;

import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(Long bookingId);

    void deleteAll();

    void deleteAllInBatch();
}
