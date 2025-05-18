package kr.hhplus.be.server.src.infra.booking.listener;

import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.ExternalDataSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExternalSaveListener {

    private final BookingRankingRepository bookingRankingRepository;

    @EventListener
    @Transactional
    @Async
    public void handle(ExternalDataSaveEvent event) {
        Booking booking = event.getBooking();
        log.info("외부 호출(mock): Booking 저장 이벤트 발생 " +
                "- Booking ID: {}, concert ID : {}, Seat Num : {},",
                booking.getBookingId(),
                booking.getConcert().getConcertId(),
                booking.getSeatNum()
        );
    }
}
