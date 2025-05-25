package kr.hhplus.be.server.src.domain.payment.event;

import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

public interface PaymentEventPublisher {

    void success(UserPointUsedEvent event);

    void success(SeatBookedCompletedEvent event);

    void success(ExternalDataSaveEvent event);
}
