package kr.hhplus.be.server.src.interfaces.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.concert.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "콘서트", description = "콘서트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {

    private final ConcertService concertService;

    @Operation(summary = "콘서트 목록 조회", description = "콘서트 목록 전체를 조회합니다.")
    @GetMapping("/list")
    public ResponseMessage<List<ConcertResponse>> getConcertList() {

        List<ConcertResponse> concertList = concertService.getConcertList();

        return ResponseMessage.success(concertList);

    }

    @Operation(summary = "콘서트 예약 가능한 좌석 조회", description = "콘서트Id를 통해 해당 콘서트의 예약 가능한 좌석을 조회합니다.")
    @Parameters({
            @Parameter(name = "concertId", required = true, description = "concertId"),
    })
    @GetMapping("/seats")
    public ResponseMessage<ConcertResponse> getAvailableSeats(@RequestParam Long concertId) {

        ConcertResponse concertResponse  = concertService.getAvailableSeats(concertId);

        return ResponseMessage.success(concertResponse);

    }
}
