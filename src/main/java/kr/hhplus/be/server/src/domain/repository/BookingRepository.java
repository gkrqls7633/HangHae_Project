package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
