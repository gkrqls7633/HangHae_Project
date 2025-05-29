package kr.hhplus.be.server.src.infra.events;

import kr.hhplus.be.server.src.domain.queue.event.QueueEventPublisher;
import kr.hhplus.be.server.src.domain.queue.event.QueuePromoteEvent;
import kr.hhplus.be.server.src.domain.queue.event.QueueTokenIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventPublisherImpl implements QueueEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_TOKEN_ISSUED_TOPIC = "user-token-issued-topic";
    private static final String QUEUE_PROMOTE_TOPIC = "queue-promote-topic";


    @Override
    public void success(QueueTokenIssuedEvent event) {
        String key = String.valueOf(event.getConcertId());
        kafkaTemplate.send(USER_TOKEN_ISSUED_TOPIC, key, event);
        log.info("-----Kafka sent QueueTokenIssuedEvent with key={} : {}-----", key, event);
    }

    @Override
    public void processTokenIssueSuccess(QueuePromoteEvent event) {
        String key = String.valueOf(event.getConcertId());
        kafkaTemplate.send(QUEUE_PROMOTE_TOPIC, key, event);
        log.info("-----Kafka sent QueuePromoteEvent with key={} : {}-----", key, event);
    }

}

