package kr.hhplus.be.server.src.domain.booking;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingResponse;

public interface BookingService{

    ResponseMessage<BookingResponse> bookingSeat(BookingRequest bookingRequest);

}
