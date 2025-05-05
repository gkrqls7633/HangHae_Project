package kr.hhplus.be.server.src.interfaces.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Schema(description = "콘서트 생성/변경 Request DTO")
@Getter
public class ConcertRequest {

    @Schema(description = "name", example = "빅뱅 2025 콘서트", required = true)
    private String name;

    @Schema(description = "price", example = "120000", required = true)
    private Long price;

    @Schema(description = "date", example = "2025-07-01", required = true)
    private String date;

    @Schema(description = "time", example = "19:00", required = true)
    private String time;

    @Schema(description = "location", example = "잠실 종합운동장", required = true)
    private String location;

    @Schema(description = "seatCnt", example = "콘서트 좌석 수", required = true)
    private int seatCnt;
}
