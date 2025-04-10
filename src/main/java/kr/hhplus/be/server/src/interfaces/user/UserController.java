package kr.hhplus.be.server.src.interfaces.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.service.UserService;
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
    public UserResponse getUserPoint(@RequestParam String userId) {
        return userService.getUserPoint(userId);
    }

}
