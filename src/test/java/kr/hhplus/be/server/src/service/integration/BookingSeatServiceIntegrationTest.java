package kr.hhplus.be.server.src.service.integration;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.QueueRepository;
import kr.hhplus.be.server.src.domain.repository.SeatRepository;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;
import kr.hhplus.be.server.src.service.BookingService;
import kr.hhplus.be.server.src.service.testTransactionHelper.BookingTransactionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@Transactional
class BookingSeatServiceIntegrationTest {

    @Autowired
    private BookingTransactionHelper bookingTransactionHelper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatRepository seatRepository;

    private BookingRequest bookingRequest;


    @BeforeEach
    void setup() {
        bookingRequest = bookingTransactionHelper.setupTestData();
    }

    @DisplayName("좌석 예약 요청 시 콘서트id, 좌석num, userId 기반으로 해당 좌석 점유 및 예약상태가 된다.")
    @Test
    void bookingSeatIntegrationTest() {

        //given
        Long concertId = bookingRequest.getConcertId();
        Long userId = bookingRequest.getUserId();
        Long seatNum = bookingRequest.getSeatNum();

        //when
        bookingService.bookingSeat(bookingRequest);

        //then : 해당 좌석이 점유상태로 변경됐는지 조회
        Optional<Seat> seatOpt = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(concertId, seatNum);
        assertTrue("좌석이 존재해야 합니다.", seatOpt.isPresent());
        Seat seat = seatOpt.get();
        assertEquals(seat.getSeatStatus(), SeatStatus.OCCUPIED);
    }
}
