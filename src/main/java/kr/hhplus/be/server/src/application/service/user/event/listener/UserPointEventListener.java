package kr.hhplus.be.server.src.application.service.user.event.listener;

import kr.hhplus.be.server.src.common.exception.PointException;
import kr.hhplus.be.server.src.common.exception.SeatException;
import kr.hhplus.be.server.src.domain.payment.event.UserPointUsedEvent;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPointEventListener {

    private final PointRepository pointRepository;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void hanlle(UserPointUsedEvent event) {
        try {
            Point point = event.getPoint();
            Long concertPrice = event.getPrice();

            point.usePoint(concertPrice);
            pointRepository.save(point);
        } catch (PointException e) {
            throw new PointException("포인트 차감 처리 중 예외 발생: " + e.getMessage());
        }
    }
}
