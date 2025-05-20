package kr.hhplus.be.server.src.application.service.payment.event.publisher;

import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.payment.event.SeatBookedCompletedEvent;
import kr.hhplus.be.server.src.domain.payment.event.UserPointUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(UserPointUsedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(SeatBookedCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void success(ExternalDataSaveEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
