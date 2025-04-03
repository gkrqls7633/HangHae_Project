package kr.hhplus.be.server.application.controller;

import kr.hhplus.be.server.application.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Booking;
import kr.hhplus.be.server.application.domain.Concert;
import kr.hhplus.be.server.application.domain.ConcertSeat;
import kr.hhplus.be.server.application.domain.SeatStatus;
import kr.hhplus.be.server.application.response.BookingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    @DisplayName("좌석 예약 테스트")
    @Test
    void bookingSeatTest() {
        //given
        BookingController controller = new BookingController();

        Concert concert = new Concert(1L, "BTS World Tour", 150000, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Booking booking = new Booking(concert, "3");
        booking.setConcertSeat(new ConcertSeat(1L, Map.of("1", SeatStatus.AVAILABLE)));

        // When
        ResponseMessage<BookingResponse> response = controller.bookingSeat(booking);

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("좌석 예약이 완료됐습니다.", response.getMessage());
    }

    @DisplayName("점유된 좌석은 예약 요청 불가능하다.")
    @Test
    void bookingSeatWithOccupiedTest() {
        //given
        BookingController controller = new BookingController();

        Concert concert = new Concert(1L, "BTS World Tour", 150000, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Booking booking = new Booking(concert, "3");
        booking.setConcertSeat(new ConcertSeat(1L, Map.of("1", SeatStatus.OCCUPIED)));

        // When
        ResponseMessage<BookingResponse> response = controller.bookingSeat(booking);

        //then
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertEquals("선택 좌석은 예약 불가능한 좌석입니다.", response.getMessage());
    }


}