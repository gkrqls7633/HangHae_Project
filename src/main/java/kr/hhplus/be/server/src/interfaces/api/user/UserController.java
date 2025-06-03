package kr.hhplus.be.server.src.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저", description = "유저 관리 API")
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Operation(summary = "유저 포인트 조회.", description = "유저의 포인트를 조회한다.")
    @GetMapping("/points")
    public UserResponse getUserPoint(@RequestParam Long userId) {
        return userService.getUserPoint(userId);
    }

    //대기 순번 조회
    @Operation(summary = "유저 대기 순번 조회.", description = "유저의 대기열 순번을 조회한다")
    @GetMapping("/queue/rank")
    public ResponseMessage<UserQueueRankResponse> getUserQueueRank(@RequestParam Long userId) {
        UserQueueRankResponse userQueueRankResponse =  userService.getUserQueueRank(userId);

        return ResponseMessage.success("유저 대기열 순번이 정상적으로 조회됐습니다.", userQueueRankResponse);
    }

    @Operation(summary = "유저 토큰 값 조회.", description = "유저의 토큰 값을 조회한다.")
    @GetMapping("/token")
    public ResponseMessage<UserQueueTokenResponse> getUserToken(@RequestParam Long userId) {
        UserQueueTokenResponse userQueueTokenResponse =  userService.getUserToken(userId);

        return ResponseMessage.success("유저 대기열 토큰 값이 정상적으로 조회됐습니다.", userQueueTokenResponse);
    }
}
