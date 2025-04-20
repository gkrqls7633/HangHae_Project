package kr.hhplus.be.server.src.interfaces.point;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "포인트", description = "포인트 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 조회", description = "포인트 잔액을 조회한다.")
    @GetMapping("")
    public ResponseMessage<PointResponse> getPoint(@RequestParam Long userId) {

        return pointService.getPoint(userId);
    }

    @Operation(summary = "포인트 충전", description = "포인트를 충전한다.")
    @PostMapping("/charge")
    public ResponseMessage<PointResponse> chargePoint(@RequestBody PointChargeRequest pointChagrgeRequest) {

        return pointService.chargePoint(pointChagrgeRequest);

    }

}
