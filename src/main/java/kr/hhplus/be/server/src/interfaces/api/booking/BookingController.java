package kr.hhplus.be.server.src.interfaces.api.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.booking.BookingService;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingCancelRequest;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingCancelResponse;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.api.booking.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "예약", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "좌석 예약 요청", description = "좌석 예약을 요청합니다.")
    @PostMapping("/seats")
    public ResponseMessage<BookingResponse> bookingSeat(@RequestBody BookingRequest bookgingRequest) {

        return bookingService.bookingSeat(bookgingRequest);
    }

    @Operation(summary = "좌석 예약 취소", description = "좌석 예약 취소 요청합니다.")
    @PutMapping("/cancel/seats")
    public ResponseMessage<BookingCancelResponse> cancelBookingSeat(@RequestBody BookingCancelRequest bookingCancelRequest) {
         return bookingService.cancelBookingSeat(bookingCancelRequest);

    }

}
