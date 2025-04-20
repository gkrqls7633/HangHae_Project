package kr.hhplus.be.server.src.infra.payment;

import kr.hhplus.be.server.src.domain.payment.Payment;
import kr.hhplus.be.server.src.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment paymentDomain) {
        return paymentJpaRepository.save(paymentDomain);
    }
}
