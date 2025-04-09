package kr.hhplus.be.server.src.interfaces.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.service.BookingService;
import org.springframework.web.bind.annotation.*;

//콘서트 예약 기능

@Tag(name = "예약", description = "예약 관련 API")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private BookingService bookingService;

    @Operation(summary = "좌석 예약 요청", description = "좌석 예약을 요청합니다.")
    @PostMapping("/seats")
    public ResponseMessage<BookingResponse> bookingSeat(@RequestBody BookingRequest bookgingRequest) {

        return bookingService.bookingSeat(bookgingRequest);
    }

    @Operation(summary = "좌석 예약 취소", description = "좌석 예약 취소 요청합니다.")
    @PutMapping("/cancel/seats")
    public ResponseMessage<BookingResponse> cancelSeat(@RequestBody Booking booking) {
        return ResponseMessage.success("좌석 예약이 취소됐습니다.");
    }
}
