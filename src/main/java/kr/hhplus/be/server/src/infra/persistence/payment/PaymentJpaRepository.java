package kr.hhplus.be.server.src.infra.persistence.payment;

import kr.hhplus.be.server.src.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
