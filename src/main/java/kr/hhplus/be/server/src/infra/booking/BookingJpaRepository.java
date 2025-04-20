package kr.hhplus.be.server.src.infra.booking;

import kr.hhplus.be.server.src.domain.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
}
