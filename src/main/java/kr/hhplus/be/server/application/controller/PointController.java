package kr.hhplus.be.server.application.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Point;
import kr.hhplus.be.server.application.domain.PointChargeRequest;
import kr.hhplus.be.server.application.domain.User;
import org.springframework.web.bind.annotation.*;


@Tag(name = "포인트", description = "포인트 관리 API")
@RestController
@RequestMapping("/point")
public class PointController {

    @Operation(summary = "포인트 조회", description = "포인트 잔액을 조회한다.")
    @GetMapping("")
    public ResponseMessage<Point> getPoint(@RequestParam String userId) {

        Point point = new Point(userId);

        if (point.getPointBalance(userId) == null) {
            point.setPointBalance(0L);
        }

        return ResponseMessage.success("포인트 잔액이 정상적으로 조회됐습니다.", point);
    }

    @Operation(summary = "포인트 충전", description = "포인트를 충전한다.")
    @PostMapping("")
    public ResponseMessage<Point> chargePoint(@RequestBody PointChargeRequest pointChagrgeRequest) {

        // todo : userId 해당하는 point 객체에 charge 처리
        User user = new User("1", "김항해", "12345", "010-1234-5678","test@navercom", "서울특별시 강서구 염창동");
        return ResponseMessage.success("포인트가 정상적으로 충전됐습니다.", user.chargePoint(pointChagrgeRequest));
    }

}
