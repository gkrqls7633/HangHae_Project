package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
