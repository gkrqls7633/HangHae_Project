package kr.hhplus.be.server.src.service.unit;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.domain.repository.SeatRepository;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
import kr.hhplus.be.server.src.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("유저의 포인트 조회 테스트")
    void bookingSeatTest() {

        // given
        BookingRequest mockBookingRequest = new BookingRequest(1L, 1L, 123L);

        Concert concert = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();

        User user = User.builder()
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();

        ConcertSeat concertSeat = new ConcertSeat();
        concertSeat.setConcert(concert);

        List<Seat> seatList = Arrays.asList(
                Seat.builder().concertSeat(concertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );
        concertSeat.setSeats(seatList);

        concert.setConcertSeat(concertSeat);

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(1L, 1L)).thenReturn(Optional.of(seatList.get(0)));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(seatRepository.save(any(Seat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseMessage<BookingResponse> response = bookingService.bookingSeat(mockBookingRequest);

        // then
        assertEquals("좌석 예약이 완료됐습니다.", response.getMessage());
        assertEquals(Optional.of(1L).get(), response.getData().getConcertId());
        assertEquals("BTS World Tour", response.getData().getConcertName());
        assertEquals(Optional.of(1L).get(), response.getData().getSeatNum());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

}
