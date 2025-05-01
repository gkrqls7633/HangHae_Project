package kr.hhplus.be.server.src.domain.seat;

import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    @DisplayName("해당 좌석은 예약 가능한 좌석이다.")
    void isAvailableSeatTest() {
        //given

        Concert concert = new Concert("BTS World Tour", 150000L, "2025-05-01", "19:00", "서울 올림픽 경기장");
        ConcertSeat mockConcertSeat = ConcertSeat.of(concert);

        Seat availableSeat  = Seat.builder().concertSeat(mockConcertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build();
        Seat bookedSeat = Seat.builder().concertSeat(mockConcertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build();
        Seat occupiedSeat = Seat.builder().concertSeat(mockConcertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build();

        //when & then
        assertTrue(availableSeat.isAvailable(), "AVAILABLE 상태의 좌석은 예약 가능하다.");
        assertFalse(bookedSeat.isAvailable(), "BOOKED 상태의 좌석은 예약 불가능하다.");
        assertFalse(occupiedSeat.isAvailable(), "OCCUPIED 상태의 좌석은 예약 불가능하다.");
    }
}