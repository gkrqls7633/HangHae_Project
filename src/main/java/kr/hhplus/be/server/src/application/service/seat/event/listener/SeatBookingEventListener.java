package kr.hhplus.be.server.src.application.service.seat.event.listener;

import kr.hhplus.be.server.src.common.exception.SeatException;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.payment.event.SeatBookedCompletedEvent;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatBookingEventListener {

    private final SeatRepository seatRepository;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(SeatBookedEvent event) {
        try {
            Seat seat = event.getSeat();
            seat.setSeatStatus(SeatStatus.OCCUPIED);
            seatRepository.save(seat);
        } catch (SeatException e) {
            throw new SeatException("좌석 점유 처리 중 예외 발생 : " + e.getStatus());
        }
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(SeatBookedCompletedEvent event) {
        try {
            Seat seat = event.getSeat();
            seat.changeBookedSeat();
            seatRepository.save(seat);
        } catch (SeatException e) {
            throw new SeatException("좌석 상태 변경 처리 중 예외 발생", e.getStatus());
        }
    }

}
