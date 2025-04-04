package kr.hhplus.be.server.application.service.port.in;

import kr.hhplus.be.server.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Booking;
import kr.hhplus.be.server.application.domain.response.BookingResponse;

public interface BookingInPort {
    ResponseMessage<BookingResponse> bookingSeat(Booking booking);
}
