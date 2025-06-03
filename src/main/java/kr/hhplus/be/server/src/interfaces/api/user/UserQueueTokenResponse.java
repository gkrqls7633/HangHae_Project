package kr.hhplus.be.server.src.interfaces.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "유저 대기열 토큰 응답")
public class UserQueueTokenResponse {

    private long userId;
    private String tokenValue;
}
