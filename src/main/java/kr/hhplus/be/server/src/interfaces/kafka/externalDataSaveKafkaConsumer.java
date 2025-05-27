package kr.hhplus.be.server.src.interfaces.kafka;

import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.external.ExternalBookingDataSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class externalDataSaveKafkaConsumer {

    @KafkaListener(topics = "external-data-save-topic", groupId = "external-data-save-consumer-group", containerFactory = "externalDataSaveListenerContainerFactory")
    @Transactional
    public void consumeExternalDataSaveEvent(ExternalBookingDataSaveEvent event, Acknowledgment ack) {
        try {
            log.info("-----Kafka Consumer received ExternalDataSaveEvent: {} -----", event);

            long bookingId = event.getBookingId();
            long concertId = event.getConcertId();

            ack.acknowledge();
            log.info("-----Kafka offset manually committed for ExternalDataSaveEvent with concertID = {} and bookingId = {}-----", concertId,bookingId);

        }  catch (Exception e) {
            log.error("Error handling ExternalDataSaveEvent: {}", e);
            throw e;
        }
    }

}
