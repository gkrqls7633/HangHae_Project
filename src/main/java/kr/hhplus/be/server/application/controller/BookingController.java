package kr.hhplus.be.server.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Booking;
import kr.hhplus.be.server.application.response.BookingResponse;
import org.springframework.web.bind.annotation.*;


@Tag(name = "예약", description = "예약 관련 API")
@RestController
@RequestMapping("/booking")
public class BookingController {

    @Operation(summary = "좌석 예약 요청", description = "좌석 예약을 요청합니다.")
    @PostMapping("")
    public ResponseMessage<BookingResponse> bookingSeat(@RequestBody Booking booking) {
        //todo : 좌석 점유 여부 체크 후 예약 가능하면 예약 요청 가능.
        if (!booking.isAvailableBooking()) {
            return ResponseMessage.error(500, "선택 좌석은 예약 불가능한 좌석입니다.");
        }

        return ResponseMessage.success("좌석 예약이 완료됐습니다.");
    }

    @Operation(summary = "좌석 예약 취소", description = "좌석 예약 취소 요청합니다.")
    @PutMapping("")
    public ResponseMessage<BookingResponse> cancelSeat(@RequestBody Booking booking) {
        return ResponseMessage.success("좌석 예약이 취소됐습니다.");
    }
}
