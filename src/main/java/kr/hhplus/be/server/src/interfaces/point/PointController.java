package kr.hhplus.be.server.src.interfaces.point;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.service.PointService;
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
    public ResponseMessage<PointResponse> getPoint(@RequestParam String userId) {

        return pointService.getPoint(userId);
    }

    @Operation(summary = "포인트 충전", description = "포인트를 충전한다.")
    @PostMapping("/charge")
    public ResponseMessage<Point> chargePoint(@RequestBody PointChargeRequest pointChagrgeRequest) {

        // todo : userId 해당하는 point 객체에 charge 처리
        User user = new User("1", "김항해", "12345", "010-1234-5678","test@navercom", "서울특별시 강서구 염창동");
        return ResponseMessage.success("포인트가 정상적으로 충전됐습니다.", user.chargePoint(pointChagrgeRequest));
    }

}
