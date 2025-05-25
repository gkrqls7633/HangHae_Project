package kr.hhplus.be.server.src.interfaces.api.booking.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingCancelResponse {

    @Schema(description = "예약 취소된 콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "예약 취소된 콘서트 이름", example = "BTS World Tour")
    private String concertName;

    @Schema(description = "예약 취소된 좌석 번호", example = "12")
    private Long seatNum;
}
