package kr.hhplus.be.server.src.interfaces.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "예약 요청 Request DTO")
@Getter
public class BookingRequest {

    @Schema(description = "concertId", example = "1", required = true)
    private Long concertId;

    @Schema(description = "seatId", example = "1",  required = false)
    private Long seatId;

    @Schema(description = "seatNum", example = "1", required = true)
    private Long seatNum;

    @Schema(description = "userId", example = "1", required = true)
    private Long userId;

}
