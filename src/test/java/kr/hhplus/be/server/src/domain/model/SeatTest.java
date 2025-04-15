package kr.hhplus.be.server.src.domain.model;

import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    @DisplayName("해당 좌석은 예약 가능한 좌석이다.")
    void isAvailableSeatTest() {
        //given

        ConcertSeat mockConcertSeat = new ConcertSeat();

        Seat availableSeat  = Seat.builder().concertSeat(mockConcertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build();
        Seat bookedSeat = Seat.builder().concertSeat(mockConcertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build();
        Seat occupiedSeat = Seat.builder().concertSeat(mockConcertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build();

        //when & then
        assertTrue(availableSeat.isAvailable(), "AVAILABLE 상태의 좌석은 예약 가능하다.");
        assertFalse(bookedSeat.isAvailable(), "BOOKED 상태의 좌석은 예약 불가능하다.");
        assertFalse(occupiedSeat.isAvailable(), "OCCUPIED 상태의 좌석은 예약 불가능하다.");
    }
}