package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingService{

    public ResponseMessage<BookingResponse> bookingSeat(Booking booking) {
        return ResponseMessage.success("좌석 예약이 완료됐습니다.");
    }
}
