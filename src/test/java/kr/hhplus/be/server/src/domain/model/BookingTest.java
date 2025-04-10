package kr.hhplus.be.server.src.domain.model;

import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingTest {

    @DisplayName("특정 좌석의 좌석 상태는 available로, 예약 가능한 좌석이다.")
    @Test
    void isAvailableBooking() {

        // given
        // 객체 조회 흐름 : concert -> concertSeat -> seats -> seatStatus

        // Mocking
        Concert mockConcert = mock(Concert.class);
        ConcertSeat mockConcertSeat = mock(ConcertSeat.class);

        List<Seat> mockSeatList = Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE),
                new Seat(4L, 4L, SeatStatus.OCCUPIED)
        );

        when(mockConcert.getConcertSeat()).thenReturn(mockConcertSeat);
        when(mockConcertSeat.getSeats()).thenReturn(mockSeatList);

        Booking booking = new Booking(1L, null, mockConcert, 1L, 1L, 1L);

        // when
        boolean isAvailableBooking = booking.isAvailableBooking();

        //then
        assertTrue(isAvailableBooking);
    }

    @DisplayName("특정 좌석의 좌석 상태는 available이 아니므로 예약이 불가능한 좌석이다.")
    @Test
    void isNotAvailableBooking() {

        // given
        // 객체 조회 흐름 : concert -> concertSeat -> seats -> seatStatus

        // Mocking
        Concert mockConcert = mock(Concert.class);
        ConcertSeat mockConcertSeat = mock(ConcertSeat.class);

        List<Seat> mockSeatList = Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE),
                new Seat(4L, 4L, SeatStatus.OCCUPIED)
        );

        when(mockConcert.getConcertSeat()).thenReturn(mockConcertSeat);
        when(mockConcertSeat.getSeats()).thenReturn(mockSeatList);

        Booking booking = new Booking(1L, null, mockConcert, 2L, 2L, 1L);

        // when
        boolean isAvailableBooking = booking.isAvailableBooking();

        //then
        assertFalse(isAvailableBooking);
    }
}