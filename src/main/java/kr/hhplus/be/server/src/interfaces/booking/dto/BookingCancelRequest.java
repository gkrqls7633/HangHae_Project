package kr.hhplus.be.server.src.interfaces.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "예약 취소 요청 Request DTO")
@Getter
public class BookingCancelRequest {

    @Schema(description = "예약된 콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "예약된 좌석 번호", example = "12")
    private Long seatNum;

}
