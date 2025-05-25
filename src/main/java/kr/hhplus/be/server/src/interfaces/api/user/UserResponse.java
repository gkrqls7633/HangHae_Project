package kr.hhplus.be.server.src.interfaces.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "유저 응답")
public class UserResponse {

    private Long userId;

    private Long point;

}
