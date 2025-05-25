package kr.hhplus.be.server.src.infra.persistence.booking;

import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public Booking save(Booking booking) {
        return bookingJpaRepository.save(booking);
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return bookingJpaRepository.findById(bookingId);
    }

    @Override
    public void deleteAll() {
        bookingJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        bookingJpaRepository.deleteAllInBatch();
    }
}
