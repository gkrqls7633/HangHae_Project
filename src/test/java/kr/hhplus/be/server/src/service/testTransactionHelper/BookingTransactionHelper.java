package kr.hhplus.be.server.src.service.testTransactionHelper;

import kr.hhplus.be.server.src.domain.model.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.*;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
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


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BookingRequest setupTestData() {
        //유저 저장
        User user = new User();
        user.setUserName("김항해");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@naver.com");
        user.setAddress("서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);

        //포인트 저장
        Point point = new Point();
        point.setUser(savedUser);
        point.setPointBalance(200000L);
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

        BookingRequest bookingRequest = new BookingRequest(savedConcert.getConcertId(), seatList.get(0).getSeatNum(), savedUser.getUserId());


        return bookingRequest;

    }

}
