package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingService{

    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;

    public ResponseMessage<BookingResponse> bookingSeat(BookingRequest bookingRequest) {


        // 1. concertId로 Concert 객체 조회
        Concert concert = concertRepository.findById(bookingRequest.getConcertId())
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다."));

        // 2. Booking 도메인 객체 생성
        Booking booking = new Booking();
        booking.setConcert(concert);
        booking.setSeatNum(bookingRequest.getSeatNum());

        // 3. 예약 가능 여부 확인 (booking의 seatNum의 좌석 점유 여부 체크)
        if (!booking.isAvailableBooking()) {
            return ResponseMessage.error(500, "선택 좌석은 예약 불가능한 좌석입니다.");
        }

        return ResponseMessage.success("좌석 예약이 완료됐습니다.");
    }
}
