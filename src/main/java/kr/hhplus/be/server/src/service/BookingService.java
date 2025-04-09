package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingService{

    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;

    private static final String mockYsno = "Y";

    public ResponseMessage<BookingResponse> bookingSeat(BookingRequest bookingRequest) {

        BookingResponse bookingResponse;

        Concert concert;
        // 1. concertId로 Concert 객체 조회
        if (mockYsno.equals("Y")) {
            List<Seat> seatList = Arrays.asList(
                    new Seat(1L, 1L, SeatStatus.AVAILABLE),
                    new Seat(2L, 2L, SeatStatus.BOOKED),
                    new Seat(3L, 3L, SeatStatus.AVAILABLE),
                    new Seat(4L, 4L, SeatStatus.OCCUPIED)
            );

            concert = new Concert(1L, "BTS World Tour", 150000, "2025-05-01", "19:00", "서울 올림픽 경기장");

            ConcertSeat concertSeat = new ConcertSeat(1L, concert, seatList);
            concertSeat.setSeats(concertSeat.getAvailableSeats());

            concert.setConcertSeat(concertSeat);

        } else {
            concert = concertRepository.findById(bookingRequest.getConcertId())
                    .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다."));
        }

        // 2. Booking 도메인 객체 생성
        Booking booking = new Booking();
        booking.setConcert(concert);
        booking.setSeatNum(bookingRequest.getSeatNum());
        booking.setUserId(bookingRequest.getUserId());

        // 3. 예약 가능 여부 확인
        //  booking의 seatNum의 좌석 점유 여부 체크
        if (!booking.isAvailableBooking()) {
            return ResponseMessage.error(500, "선택 좌석은 예약 불가능한 좌석입니다.");

//        } else if () {
//            //todo : queuing token 확인 후 대기 순번 체크. 해당 차례 됐을 때 예약 서비스 이용 가능
//

        } else {

            if (mockYsno.equals("Y")) {
                bookingResponse = BookingResponse.builder()
                        .concertId(booking.getConcert().getConcertId())
                        .concertName(booking.getConcert().getName())
                        .seatNum(booking.getSeatNum())
                        .bookingMessage("좌석 예약이 완료됐습니다.")
                        .build();

                return ResponseMessage.success("좌석 예약이 완료됐습니다.", bookingResponse);
            } else{
                bookingRepository.save(booking);
            }

        }

        return ResponseMessage.success("좌석 예약이 완료됐습니다.", new BookingResponse());
    }
}
