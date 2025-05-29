package kr.hhplus.be.server.src.infra.kafka;

import kr.hhplus.be.server.src.domain.queue.event.QueueEventPublisher;
import kr.hhplus.be.server.src.domain.queue.event.QueueTokenIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaQueueEventPublisher implements QueueEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_TOKEN_ISSUED_TOPIC = "user-token-issued-topic";


    @Override
    public void success(QueueTokenIssuedEvent event) {
        String key = String.valueOf(event.getConcertId());
        kafkaTemplate.send(USER_TOKEN_ISSUED_TOPIC, key, event);
        log.info("-----Kafka sent QueueTokenIssuedEvent with key={} : {}-----", key, event);
    }
}

