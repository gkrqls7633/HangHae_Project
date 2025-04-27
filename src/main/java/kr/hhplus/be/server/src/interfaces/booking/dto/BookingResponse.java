package kr.hhplus.be.server.src.interfaces.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "좌석 예약 응답")
public class BookingResponse {

    @Schema(description = "예약된 콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "예약된 콘서트 이름", example = "BTS World Tour")
    private String concertName;

    @Schema(description = "예약된 좌석 번호", example = "12")
    private Long seatNum;

    @Schema(description = "예약 상태 메시지", example = "좌석 예약이 완료되었습니다.")
    private String bookingMessage;



}
