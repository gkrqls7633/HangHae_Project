package kr.hhplus.be.server.src.domain.booking;

import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.user.User;
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
        ConcertSeat mockConcertSeat = mock(ConcertSeat.class);
        Concert mockConcert = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .concertSeat(mockConcertSeat)
                .build();
        User mockUser = mock(User.class);

        List<Seat> mockSeatList = Arrays.asList(
                Seat.builder().concertSeat(mockConcertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );

        when(mockConcertSeat.getSeats()).thenReturn(mockSeatList);

        Booking booking = new Booking(1L, mockConcert, 1L, 1L, mockUser);

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
        ConcertSeat mockConcertSeat = mock(ConcertSeat.class);
        Concert mockConcert = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .concertSeat(mockConcertSeat)
                .build();
        User mockUser = mock(User.class);

        List<Seat> mockSeatList = Arrays.asList(
                Seat.builder().concertSeat(mockConcertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );

        when(mockConcertSeat.getSeats()).thenReturn(mockSeatList);

        Booking booking = new Booking(1L, mockConcert, 2L, 2L, mockUser);


        // when
        boolean isAvailableBooking = booking.isAvailableBooking();

        //then
        assertFalse(isAvailableBooking);
    }
}