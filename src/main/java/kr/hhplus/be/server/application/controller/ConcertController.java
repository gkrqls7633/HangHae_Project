package kr.hhplus.be.server.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Concert;
import kr.hhplus.be.server.application.domain.ConcertSeat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@Tag(name = "콘서트", description = "콘서트 API")
@RestController
@RequestMapping("/concert")
public class ConcertController {

    @Operation(summary = "콘서트 목록 조회", description = "콘서트 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseMessage<List<Concert>> getConcertList() {
        List<Concert> concertList = Arrays.asList(
                new Concert(1L, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장"),
                new Concert(2L, "IU Love Poem", 130000, "2024-06-10", "18:30", "부산 사직 경기장"),
                new Concert(3L, "Coldplay Music of the Spheres", 180000, "2024-07-20", "20:00", "인천 아시아드 주경기장")
        );

        return ResponseMessage.success(concertList);
    }

    @Operation(summary = "콘서트 예약 가능한 날짜 조회", description = "콘서트 예약 가능한 날짜를 조회합니다.")
    @Parameters({
            @Parameter(name = "concertId", required = true, description = "concertId"),
    })
    @ApiResponse(
            responseCode = "200",
            description = "예약 가능한 날짜를 반환합니다.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{ \"status\": 200, \"message\": \"성공\", \"data\": \"2024-05-01\" }")
            )
    )
    @GetMapping("/available-date")
    public ResponseMessage<String> getAvailableDate(@RequestParam Long concertId) {
        Concert concert = new Concert(1L, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장");

        return ResponseMessage.success(concert.getDate());
    }

    @Operation(summary = "콘서트 예약 가능한 좌석 조회", description = "콘서트 예약 가능한 좌석을 조회합니다.")
    @Parameters({
            @Parameter(name = "concertId", required = true, description = "concertId"),
    })
    @GetMapping("/available-seat")
    public ResponseMessage<ConcertSeat> getAvailableSeat(@RequestParam Long concertId) {
        List<String> seatList = Arrays.asList("1", "2", "3", "4");

        ConcertSeat concertSeat = new ConcertSeat(concertId, seatList);

        return ResponseMessage.success(concertSeat);
    }
}
