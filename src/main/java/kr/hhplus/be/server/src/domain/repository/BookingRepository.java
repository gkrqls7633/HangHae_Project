package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.infrastructure.repository.BookingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
}
