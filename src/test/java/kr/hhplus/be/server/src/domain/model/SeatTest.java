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
        Seat availableSeat  = new Seat(1L, 1L, SeatStatus.AVAILABLE);
        Seat bookedSeat = new Seat(2L, 2L, SeatStatus.BOOKED);
        Seat occupiedSeat = new Seat(3L, 3L, SeatStatus.OCCUPIED);

        //when & then
        assertTrue(availableSeat.isAvailable(), "AVAILABLE 상태의 좌석은 예약 가능하다.");
        assertFalse(bookedSeat.isAvailable(), "BOOKED 상태의 좌석은 예약 불가능하다.");
        assertFalse(occupiedSeat.isAvailable(), "OCCUPIED 상태의 좌석은 예약 불가능하다.");
    }
}