package kr.hhplus.be.server.src.domain.booking;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingCancelRequest;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingCancelResponse;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingResponse;

public interface BookingService{

    ResponseMessage<BookingResponse> bookingSeat(BookingRequest bookingRequest);

    ResponseMessage<BookingResponse> bookingSeatWithLock(BookingRequest bookgingRequest);

    ResponseMessage<BookingResponse> bookingSeatWithPessimisticLock(BookingRequest bookgingRequest);

    ResponseMessage<BookingCancelResponse> cancelBookingSeat(BookingCancelRequest bookingCancelRequest);
}
