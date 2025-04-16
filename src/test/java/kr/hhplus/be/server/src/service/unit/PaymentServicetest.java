package kr.hhplus.be.server.src.service.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.domain.repository.PaymentRepository;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentServicetest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {

        Concert concert = Concert.builder()
                .concertId(1L)
                .name("BTS World Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();

        User user = User.builder()
                .userId(1L)
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();

        Point point = Point.builder()
                .user(user)
                .pointBalance(200000L)
                .build();

        Booking booking = Booking.builder()
                .bookingId(1L)
                .concert(concert)
                .seatNum(1L)
                .seatId(1L)
                .user(user)
                .build();

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

}
