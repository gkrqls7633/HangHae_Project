package kr.hhplus.be.server.src.domain.booking.unit;

import kr.hhplus.be.server.src.application.service.booking.BookingServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import kr.hhplus.be.server.src.application.service.booking.event.publisher.BookingEventPublisher;
import kr.hhplus.be.server.src.domain.booking.event.ConcertBookingScoreIncrementEvent;
import kr.hhplus.be.server.src.domain.booking.event.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingResponse;
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
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

//    @Mock
//    private QueueRepository queueRepository;

    @Mock
    private RedisQueueRepository redisQueueRepository;

//    @Mock
//    private BookingRankingRepository bookingRankingRepository;

    @Mock
    private BookingEventPublisher bookingEventPublisher;


    @Test
    @DisplayName("유저 콘서트 좌석 예약 시 좌석이 정상 예약되고, 콘서트 예약 점수 레디스 정상 호출한다.")
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

        Queue queue = Queue.newToken(123L);

        ConcertSeat concertSeat = ConcertSeat.of(concert);
        List<Seat> seatList = Arrays.asList(
                Seat.builder().concertSeat(concertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );
        concertSeat.setSeats(seatList);

        concert.setConcertSeat(concertSeat);

        when(redisQueueRepository.findByUserIdAndTokenStatus(123L, TokenStatus.ACTIVE)).thenReturn(Optional.of(queue));
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(1L, 1L)).thenReturn(Optional.of(seatList.get(0)));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(bookingEventPublisher).success(any(SeatBookedEvent.class));
        doNothing().when(bookingEventPublisher).success(any(ConcertBookingScoreIncrementEvent.class));
        doNothing().when(bookingEventPublisher).success(any(ExternalDataSaveEvent.class));

        // when
        ResponseMessage<BookingResponse> response = bookingService.bookingSeat(mockBookingRequest);

        // then
        assertEquals("좌석 예약이 완료됐습니다.", response.getMessage());
        assertEquals(Optional.of(1L).get(), response.getData().getConcertId());
        assertEquals("BTS World Tour", response.getData().getConcertName());
        assertEquals(Optional.of(1L).get(), response.getData().getSeatNum());

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingEventPublisher, times(1)).success(any(SeatBookedEvent.class));
        verify(bookingEventPublisher, times(1)).success(any(ConcertBookingScoreIncrementEvent.class));
        verify(bookingEventPublisher, times(1)).success(any(ExternalDataSaveEvent.class));

    }

}
