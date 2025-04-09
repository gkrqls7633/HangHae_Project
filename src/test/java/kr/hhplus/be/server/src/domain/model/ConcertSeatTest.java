package kr.hhplus.be.server.src.domain.model;

import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertSeatTest {

    @Mock
    ConcertSeat concertSeat;

    @DisplayName("특정 콘서트의 예약 가능한 좌석을 조회한다.")
    @Test
    void getOnlyAvailableSeatsTest() {
        //given
        Long concertId = 1L;
        ConcertSeat concertSeat = new ConcertSeat();

        Concert concert = new Concert(concertId, "BTS World Tour", 150000, "2025-05-01", "19:00", "서울 올림픽 경기장");
        List<Seat> seatList = Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE),
                new Seat(4L, 4L, SeatStatus.OCCUPIED)
        );
        concertSeat.setSeats(seatList);

        //when
        //예약 가능한 좌석만 조회
        //concert -> concertSeat -> seat -> seatStatus
        List<Seat> availableSeatList = concertSeat.getAvailableSeats();

        //then
        assertEquals(2, availableSeatList.size());
        assertTrue(availableSeatList.stream().allMatch(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE));
    }

    @DisplayName("특정 콘서트의 예약 불가능한 좌석이 조회된다.")
    @Test
    void getNotAvailableSeatsTest() {
        //given
        Long concertId = 1L;
//        ConcertSeat concertSeat = new ConcertSeat();

        Concert concert = new Concert(concertId, "BTS World Tour", 150000, "2025-05-01", "19:00", "서울 올림픽 경기장");
        List<Seat> seatList = Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE),
                new Seat(4L, 4L, SeatStatus.OCCUPIED)
        );
        concertSeat.setSeats(seatList);

        when(concertSeat.getAvailableSeats()).thenReturn(Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE)
        ));

        //when
        List<Seat> availableSeatList = concertSeat.getAvailableSeats();

        //then
        assertNotEquals(2, availableSeatList.size());
        assertTrue(availableSeatList.stream().anyMatch(seat -> seat.getSeatStatus() != SeatStatus.AVAILABLE),
                "Available 상태가 아닌 좌석이 포함되어 있습니다.");
    }

}