package kr.hhplus.be.server.src.interfaces.user;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "유저 대기열 순위 응답")
public class UserQueueRankResponse {

    private long userId;
    private int rank;
}
