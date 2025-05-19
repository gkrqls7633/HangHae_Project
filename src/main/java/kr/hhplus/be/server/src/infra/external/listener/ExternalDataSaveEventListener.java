package kr.hhplus.be.server.src.infra.external.listener;

import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalDataSaveEventListener {

    @EventListener
    @Async
    @Transactional
    public void handle(ExternalDataSaveEvent event) {
        Object entity = event.domainEntity();

        if (entity instanceof Booking booking) {
            handleBooking(booking);
        } else if (entity instanceof Payment payment) {
            handlePayment(payment);
        } else {
            log.warn("지원하지 않는 외부 이벤트 타입: {}", entity.getClass().getSimpleName());
        }
    }

    private void handleBooking(Booking booking) {
        log.info("외부 호출(mock): Booking 저장 이벤트 발생 - Booking ID: {}, concert ID : {}, Seat Num : {}",
                booking.getBookingId(),
                booking.getConcert().getConcertId(),
                booking.getSeatNum()
        );
        // 실제 외부 연동 API 호출 코드 자리 (mock)
    }

    private void handlePayment(Payment payment) {
        log.info("외부 호출(mock): Payment 저장 이벤트 발생 - Payment ID: {}, 상태: {}",
                payment.getPaymentId(),
                payment.getPaymentStatus()
        );
        // 실제 외부 연동 API 호출 코드 자리 (mock)
    }
}
