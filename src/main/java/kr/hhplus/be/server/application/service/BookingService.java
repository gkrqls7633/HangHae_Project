package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Booking;
import kr.hhplus.be.server.application.domain.response.BookingResponse;
import kr.hhplus.be.server.application.service.port.in.BookingInPort;
import org.springframework.stereotype.Service;

@Service
public class BookingService implements BookingInPort {

    @Override
    public ResponseMessage<BookingResponse> bookingSeat(Booking booking) {
        return ResponseMessage.success("좌석 예약이 완료됐습니다.");
    }
}
