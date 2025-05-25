package kr.hhplus.be.server.src.infra.events;

import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.payment.event.PaymentEventPublisher;
import kr.hhplus.be.server.src.domain.payment.event.SeatBookedCompletedEvent;
import kr.hhplus.be.server.src.domain.payment.event.UserPointUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void success(UserPointUsedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void success(SeatBookedCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void success(ExternalDataSaveEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
