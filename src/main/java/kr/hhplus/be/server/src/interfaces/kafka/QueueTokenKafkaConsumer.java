package kr.hhplus.be.server.src.interfaces.kafka;

import kr.hhplus.be.server.src.domain.queue.QueueService;
import kr.hhplus.be.server.src.domain.queue.QueueTokenService;
import kr.hhplus.be.server.src.domain.queue.event.QueueEventPublisher;
import kr.hhplus.be.server.src.domain.queue.event.QueuePromoteEvent;
import kr.hhplus.be.server.src.domain.queue.event.QueueTokenIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueTokenKafkaConsumer {

    private final QueueTokenService queueTokenService;
    private final QueueEventPublisher queueEventPublisher;


    @KafkaListener(topics = "user-token-issued-topic", groupId = "token-issued-consumer-group", containerFactory = "userTokenIssuedListenerContainerFactory")
    public void consumeUserTokenIssuedEvent(QueueTokenIssuedEvent event, Acknowledgment ack) {
        try {
            log.info("-----Kafka Consumer received ExternalDataSaveEvent: {} -----", event);

            long userId = event.getUserId();
            long concertId = event.getConcertId();

            queueTokenService.processTokenIssue(event.getUserId(), event.getConcertId());

            /*
            kafka 'queue-promote' 토픽 이벤트 발행
             - 토큰 활성화 (ready -> active)
            */
            queueEventPublisher.processTokenIssueSuccess(new QueuePromoteEvent(userId, concertId));

            ack.acknowledge();
            log.info("-----Kafka offset manually committed for QueueTokenIssuedEvent with userId = {} and concertId = {}-----", userId, concertId);

        }  catch (Exception e) {
            log.error("Error handling QueueTokenIssuedEvent: {}", e);
            throw e;
        }
    }
}
