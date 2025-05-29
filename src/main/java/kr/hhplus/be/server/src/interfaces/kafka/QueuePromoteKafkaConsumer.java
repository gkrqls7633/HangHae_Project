package kr.hhplus.be.server.src.interfaces.kafka;

import kr.hhplus.be.server.src.domain.queue.QueueTokenService;
import kr.hhplus.be.server.src.domain.queue.event.QueuePromoteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueuePromoteKafkaConsumer {

    private final QueueTokenService queueTokenService;

    @KafkaListener(topics = "queue-promote-topic", groupId = "queue-promote-consumer-group", containerFactory = "queuePromoteListenerContainerFactory")
    public void consumeQueuePromoteEvent(QueuePromoteEvent event, Acknowledgment ack) {

        try {
            log.info("-----Kafka Consumer received QueuePromoteEvent: {} -----", event);

            long userId = event.getUserId();
            long concertId = event.getConcertId();

            queueTokenService.processQueuePromote(event.getUserId(), event.getConcertId());

            ack.acknowledge();
            log.info("-----Kafka offset manually committed for QueuePromoteEvent with userId = {} and concertId = {}-----", userId, concertId);

        }  catch (Exception e) {
            log.error("Error handling QueuePromoteEvent: {}", e);
            throw e;
        }

    }
}
