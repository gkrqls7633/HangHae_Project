package kr.hhplus.be.server.src.interfaces.kafka;

import kr.hhplus.be.server.src.common.exception.SeatException;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatBookedKafkaConsumer {

    private final SeatRepository seatRepository;

    @KafkaListener(topics = "seat-booked-topic", groupId = "concert-consumer-group", containerFactory = "seatBookedListenerContainerFactory")
    @Transactional
    public void consumeSeatBookedEvent(SeatBookedEvent event, Acknowledgment ack) {
        try {
            log.info("-----Kafka Consumer received SeatBookedEvent: {} -----", event);

            Seat seat = event.getSeat();
            seat.setSeatStatus(SeatStatus.OCCUPIED);
            seat.setConcertSeat(event.getConcertSeat());
            seatRepository.save(seat);

            // 수동 커밋 (성공적으로 처리된 경우에만)
            ack.acknowledge();
            log.info("-----Kafka offset manually committed for SeatBookedEvent with seatId={}-----", seat.getSeatId());

        } catch (SeatException e) {
            log.error("Error handling SeatBookedEvent: {}", e.getStatus(), e);
            throw e;
        }
    }

}
