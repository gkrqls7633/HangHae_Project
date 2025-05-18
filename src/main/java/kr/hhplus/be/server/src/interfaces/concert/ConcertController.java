package kr.hhplus.be.server.src.interfaces.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.concert.ConcertRankingService;
import kr.hhplus.be.server.src.domain.concert.ConcertService;
import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertBookingRankResponse;
import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertInfoResponse;
import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertRequest;
import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "콘서트", description = "콘서트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertRankingService concertRankingService;

    @Operation(summary = "콘서트 목록 조회", description = "콘서트 목록 전체를 조회합니다.")
    @GetMapping("/list")
    public ResponseMessage<List<ConcertInfoResponse>> getConcertList() {

        List<ConcertInfoResponse> concertList = concertService.getConcertList();

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

    //todo : 신규 콘서트 기능 추가 -> 캐시 만료 필요
    @Operation(summary = "콘서트 신규 추가", description = "신규 콘서트를 추가한다.")
    @PostMapping("")
    public ResponseMessage<ConcertResponse> createConcert(@RequestBody ConcertRequest concertRequest) {

        ConcertResponse concertResponse  = concertService.createConcert(concertRequest);

        return ResponseMessage.success(concertResponse);
    }


    //todo : 콘서트 일정 등등 변경 기능 추가 -> 캐시 만료 필요
    @Operation(summary = "콘서트 정보 변경", description = "콘서트 정보를 변경한다.")
    @PutMapping("")
    public ResponseMessage<ConcertResponse> updateConcert(@RequestBody ConcertRequest concertRequest) {

        ConcertResponse concertResponse  = concertService.updateConcert(concertRequest);

        return ResponseMessage.success(concertResponse);
    }

    @Operation(summary = "콘서트 예약 랭킹 순위 조회", description = "콘서트 매진 랭킹 순위를 조회한다.")
    @GetMapping("ranking")
    public ResponseMessage<ConcertBookingRankResponse> getConcertBookingRank() {
        ConcertBookingRankResponse concertBookingRankResponse = concertRankingService.getConcertBookingRank();

        return ResponseMessage.success("콘서트 매진 랭킹 순위가 정상 조회됐습니다.", concertBookingRankResponse);
    }

}
