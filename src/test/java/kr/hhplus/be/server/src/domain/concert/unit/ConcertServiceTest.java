package kr.hhplus.be.server.src.domain.concert.unit;

import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeatRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.interfaces.concert.ConcertResponse;
import kr.hhplus.be.server.src.domain.concert.ConcertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;

    @Test
    @DisplayName("콘서트 목록을 조회한다.")
    void getConcertListTest() {

        // given
        Concert concert1 = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();

        Concert concert2 = Concert.builder()
                .concertId(2L)
                .name("IU 콘서트")
                .price(120000L)
                .date("2025-06-15")
                .time("18:00")
                .location("부산 사직 체육관")
                .build();

        List<Concert> concertList = List.of(concert1, concert2);

        // when
        when(concertRepository.findAll()).thenReturn(concertList);

        // then
        List<ConcertResponse> result = concertService.getConcertList();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("콘서트 리스트가 비어있을 경우 빈 리스트 반환한다.")
    void getConcertEmptyListTest() {
        // given
        when(concertRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<ConcertResponse> result = concertService.getConcertList();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("예약 가능한 좌석 리스트를 조회한다.")
    void getAvailableSeatsTest() {

        // given
        Long concertId = 1L;

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

        List<Seat> mockSeatList = Arrays.asList(
                Seat.builder().concertSeat(mockConcertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(mockConcertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );

        List<Seat> availableSeats = mockSeatList.stream()
                .filter(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(mockConcert));
        when(concertSeatRepository.findAllSeatsByConcertId(concertId)).thenReturn(availableSeats);

        //when
        ConcertResponse response = concertService.getAvailableSeats(concertId);

        //then
        assertThat(response.getSeatList().size()).isEqualTo(2);
    }

}
