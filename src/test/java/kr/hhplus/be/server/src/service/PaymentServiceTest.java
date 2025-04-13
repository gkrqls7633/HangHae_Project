package kr.hhplus.be.server.src.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.*;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Long savedBookingId;
    private Long savedUserId;

    @Transactional
    @BeforeEach
    void setup() {
        // 1. 테스트용 유저 생성
        User user = new User();
        user.setUserName("김항해");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@naver.com");
        user.setAddress("서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);

        savedUserId = savedUser.getUserId();


        //2. 포인트 생성
        Point point = new Point();
        point.setUser(user);
        point.setPointBalance(200000L);
        pointRepository.save(point);

        // 3. 콘서트 생성
        Concert concert = new Concert();
        concert.setName("BTS World Tour");
        concert.setPrice(150000L);
        concert.setDate("2025-05-01");
        concert.setTime("19:00");
        concert.setLocation("서울 올림픽 경기장");

        ConcertSeat concertSeat = new ConcertSeat();
        concertSeat.setConcert(concert);
        concertRepository.save(concert);

        List<Seat> seats = Arrays.asList(
                new Seat(1L, SeatStatus.AVAILABLE, concertSeat),
                new Seat(2L, SeatStatus.AVAILABLE, concertSeat)
        );

        concertSeat.setSeats(seats);
        concertSeatRepository.save(concertSeat);

        // 4. 예약 생성 (Booking)
        Booking booking = new Booking();
        booking.setUser(user); // 유저와 연결
        booking.setConcert(concert); // 콘서트와 연결
        booking.setSeatId(1L); // 좌석 ID 설정
        booking.setSeatNum(1L); // 좌석 번호 설정
        Booking savedBooking =  bookingRepository.save(booking);

        savedBookingId = savedBooking.getBookingId();
    }

    @Test
    @Commit
    void 결제_정상_처리_성공() {
        // given
        PaymentRequest request = new PaymentRequest();
        request.setBookingId(savedBookingId);
        request.setUserId(savedUserId);

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("예약 내역이 존재하지 않습니다."));

        // when
        ResponseMessage<PaymentResponse> response = paymentService.processPayment(request);

        // then
        Point point = pointRepository.findById(savedUserId).get();
        assertEquals(50000L, point.getPointBalance()); // 200000 - 150000
    }

}