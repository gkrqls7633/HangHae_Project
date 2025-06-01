package kr.hhplus.be.server.src.infra.events;

import kr.hhplus.be.server.src.domain.booking.event.BookingEventPublisher;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import kr.hhplus.be.server.src.domain.external.ExternalBookingDataSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisherImpl implements BookingEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SEAT_BOOKED_TOPIC = "seat-booked-topic";
    private static final String EXTERNAL_DATA_SAVE_TOPIC = "external-data-save-topic";

    @Override
    public void success(SeatBookedEvent event) {
        //key 값에 따라 메시지가 각기 다른 파티션으로 분배되게. (콘서트 별로 key 구성 -> 자동으로 kafkaTemplate이 hash 변환)
        String key = String.valueOf(event.getSeat().getConcertSeat().getConcert().getConcertId());
        kafkaTemplate.send(SEAT_BOOKED_TOPIC, key, event);
        log.info("-----Kafka sent SeatBookedEvent with key={} : {}-----", key, event);
    }
    public void success(ConcertBookingScoreIncrementEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void success(ExternalBookingDataSaveEvent event) {
        //key 값에 따라 메시지가 각기 다른 파티션으로 분배되게. (concert_id + booking_id로  key 구성)
        long bookingId = event.getBookingId();
        long concertId = event.getConcertId();
        String key = concertId+ "_" + bookingId;

        kafkaTemplate.send(EXTERNAL_DATA_SAVE_TOPIC, key, event);
        log.info("-----Kafka sent ExternalBookingDataSaveEvent with key={} : {}-----", key, event);
    }

}
