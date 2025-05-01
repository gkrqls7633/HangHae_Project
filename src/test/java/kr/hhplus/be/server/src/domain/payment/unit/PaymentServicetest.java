package kr.hhplus.be.server.src.domain.payment.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.application.service.PaymentServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.common.exception.PaymentException;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.payment.Payment;
import kr.hhplus.be.server.src.domain.payment.PaymentRepository;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServicetest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private SeatRepository seatRepository;

    private Concert mockConcert;
    private User mockUser;
    private Point mockPoint;
    private Booking mockBooking;
    private Seat mockSeat;

    @BeforeEach
    void setUp() {

        mockConcert = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();

        mockUser = User.builder()
                .userId(1L)
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();

        mockPoint = Point.builder()
                .user(mockUser)
                .pointBalance(200000L)
                .build();

        mockBooking = Booking.builder()
                .bookingId(1L)
                .concert(mockConcert)
                .seatNum(1L)
                .seatId(1L)
                .user(mockUser)
                .build();

        mockSeat = Seat.builder()
                .seatId(mockBooking.getSeatId())
                .seatStatus(SeatStatus.AVAILABLE) // 또는 SeatStatus.OCCUPIED
                .build();

    }

    @DisplayName("좌석 상태를 booked로 변경한다.")
    @Test
    void changeBookedSeat() {

        //given
        mockSeat.setSeatStatus(SeatStatus.OCCUPIED);

        //when
        mockSeat.changeBookedSeat();

        //then
        assertEquals(mockSeat.getSeatStatus(), SeatStatus.BOOKED);

    }

    @DisplayName("예약 내역이 존재하지 않으면 에러 발생한다.")
    @Test
    void noBookingErrorTest() {

        //given
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(2L);
        paymentRequest.setUserId(1L);

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });
    }

    @DisplayName("결제 요청 최초 진입 시 결제 대기상태를 먼저 저장한다.")
    @Test
    void paymentPendingSavetest() {

        //given
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        given(bookingRepository.findById(1L)).willReturn(Optional.of(mockBooking));

        // save 호출되면 예외 던져서 이후 로직 진행 막음
        given(paymentRepository.save(any(Payment.class)))
                .willAnswer(invocation -> {
                    throw new RuntimeException("테스트용 중단");
                });

        // when & then
        assertThrows(RuntimeException.class, () -> paymentService.processPayment(paymentRequest));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


    @DisplayName("결제 요청 유저와 예약된 유저가 동일하지 않으면 에러 발생한다.")
    @Test
    void paymentUserDiffBookedUserTest() {

        //given
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(99L);  //결제 요청 유저 id가 다름

        given(bookingRepository.findById(1L)).willReturn(Optional.of(mockBooking));

        // when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("유저 정보가 일치하지 않습니다.", exception.getMessage());

    }

    @DisplayName("결제할 콘서트의 예약 좌석이 존재하지 않으면 실패한다.")
    @Test
    void bookingNoSeatErrorTest() {

        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.empty());

        //when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("해당 좌석이 존재하지 않습니다.", exception.getMessage());

    }

    @DisplayName("결제할 콘서트의 예약 좌석 점유 여부를 체크한다.")
    @Test
    void bookingOccupiedOrBookedSeatErrorTest() {
        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        Seat mockSeat = Seat.builder()
                .seatId(mockSeatId)
                .seatStatus(SeatStatus.BOOKED) // 또는 SeatStatus.OCCUPIED
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(mockSeat));

        //when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("좌석 예약 내역을 재확인 해주세요.", exception.getMessage());
    }

    @DisplayName("결제할 콘서트의 가격 조회 시 정보 없으면 에러난다.")
    @Test
    void noConcertInfoErrorTest() {
        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(mockSeat));
        when(concertRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("콘서트 정보가 없습니다.", exception.getMessage());

    }

    @DisplayName("결제할 유저의 포인트 정보 조회 시 정보 없으면 에러난다.")
    @Test
    void noPointInfoErrorTest() {
        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(mockSeat));
        when(concertRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockConcert));
        when(pointRepository.findById(anyLong())).thenReturn(Optional.empty());


        // when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("사용자 포인트 정보가 없습니다.", exception.getMessage());
    }

    @DisplayName("결제할 유저의 포인트가 부족하면 에러난다.")
    @Test
    void notEnoughPointTest() {
        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        Point point = Point.of(1L, 50000L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(mockSeat));
        when(concertRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockConcert));
        when(pointRepository.findById(anyLong())).thenReturn(Optional.of(point));

        // when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("잔액을 확인해주세요.", exception.getMessage());
    }

    @DisplayName("유저의 포인트가 충분하면 결제가 완료된다.")
    @Test
    void enoughPointPaymentTest() {

        //given
        Long mockSeatId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setUserId(1L);

        Point point = Point.of(1L, 50000L);

        Payment savedPayment = Payment.builder()
                .paymentId(999L)
                .bookingId(1L)
                .userId(1L)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(mockBooking));
        when(seatRepository.findById(mockSeatId)).thenReturn(Optional.of(mockSeat));
        when(concertRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockConcert));
        when(pointRepository.findById(anyLong())).thenReturn(Optional.of(mockPoint));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        //when
        ResponseMessage<PaymentResponse> response = paymentService.processPayment(paymentRequest);

        //then
        assertEquals(50000L ,point.getPointBalance()); //포인트 잔액 차감 확인
        assertEquals("결제가 완료됐습니다.", response.getMessage());
        assertEquals(PaymentStatus.COMPLETED, response.getData().getPaymentStatus());
        assertEquals(999L, response.getData().getPaymentId());
        assertEquals(1L, response.getData().getBookingId());
    }

}
