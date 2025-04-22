package kr.hhplus.be.server.src.domain.booking.integration;

import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeatRepository;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class BookingTransactionHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    @Autowired
    private QueueRepository queueRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingRequest setupTestData() {
        //유저 저장
        User user = User.of("김항해"
                , "12345"
                ,"010-1234-5678"
                ,"test@naver.com"
                , "서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);

        //포인트 저장
        Point point = Point.of(savedUser.getUserId(), savedUser, 200000L);
        pointRepository.save(point);
//        pointRepository.flush();

        //콘서트 저장
        Concert concert = Concert.builder()
                .name("BTS Workd Tour")
                .price(150000L)
                .date("2025-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .build();
        Concert savedConcert = concertRepository.save(concert);

        ConcertSeat concertSeat = ConcertSeat.of(savedConcert);
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
        Queue queue = Queue.newToken();
        queue.setUserId(savedUser.getUserId());

        //활성화 토큰으로 변경
        queue.setTokenStatus(TokenStatus.ACTIVE);
        queueRepository.save(queue);

        BookingRequest bookingRequest = new BookingRequest(savedConcert.getConcertId(), seatList.get(0).getSeatNum(), savedUser.getUserId());

        return bookingRequest;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User createUser(Long num) {

        User user = User.of("김항해" + num
                , "12345"
                ,"010-1234-5678"
                ,"test@naver.com"
                , "서울특별시 강서구 염창동");

        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Queue createQueue(User user) {
        //유저 대기 토큰 발급
        Queue queue = Queue.newToken();
        queue.setUserId(user.getUserId());

        //활성화 토큰으로 변경
        queue.setTokenStatus(TokenStatus.ACTIVE);
        return queueRepository.save(queue);
    }

}
