package kr.hhplus.be.server.src.service.testTransactionHelper;

import kr.hhplus.be.server.src.domain.model.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.*;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class PaymentTransactionHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentRequest setupTestData() {
        //유저 저장
        User user = User.builder()
//                .userId(1L)
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();
        User savedUser = userRepository.save(user);

        //포인트 저장
        Point point = Point.builder()
                .user(savedUser)
                .pointBalance(200000L)
                .build();
        pointRepository.save(point);
        pointRepository.flush();

        //콘서트 저장
        Concert concert = Concert.builder()
                .name("BTS Workd Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();
        Concert savedConcert = concertRepository.save(concert);

        ConcertSeat concertSeat = new ConcertSeat();
        concertSeat.setConcert(savedConcert);

        List<Seat> seatList = Arrays.asList(
                Seat.builder().concertSeat(concertSeat).seatNum(1L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(2L).seatStatus(SeatStatus.BOOKED).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(3L).seatStatus(SeatStatus.AVAILABLE).build(),
                Seat.builder().concertSeat(concertSeat).seatNum(4L).seatStatus(SeatStatus.OCCUPIED).build()
        );
        concertSeat.setSeats(seatList);

        //콘서트-시트 먼저 저장
        concertSeatRepository.save(concertSeat);

        //좌석 저장
        seatRepository.saveAll(seatList);

        //유저 대기 토큰 발급
        Queue queue = new Queue();
        queue.setUserId(savedUser.getUserId());
        queue.newToken(); //발급

        //활성화 토큰으로 변경
        queue.setTokenStatus(TokenStatus.ACTIVE);
        queueRepository.save(queue);

        //예약내역 저장
        Booking booking = Booking.builder()
                .concert(savedConcert)
                .seatNum(1L)
                .seatId(1L)
                .user(savedUser)
                .build();
        bookingRepository.save(booking);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(booking.getBookingId());
        paymentRequest.setUserId(savedUser.getUserId());

        return paymentRequest;
    }
}
