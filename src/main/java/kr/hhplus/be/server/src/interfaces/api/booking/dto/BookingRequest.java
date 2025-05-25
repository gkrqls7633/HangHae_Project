package kr.hhplus.be.server.src.interfaces.api.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "예약 요청 Request DTO")
@Getter
@NoArgsConstructor
public class BookingRequest {

    @Schema(description = "concertId", example = "1", required = true)
    private Long concertId;

    @Schema(description = "seatNum", example = "1", required = true)
    private Long seatNum;

    @Schema(description = "userId", example = "1", required = true)
    private Long userId;

    public BookingRequest(Long concertId, Long seatNum, Long userId) {
        this.concertId = concertId;
        this.seatNum = seatNum;
        this.userId = userId;
    }

}
