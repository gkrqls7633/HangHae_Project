package kr.hhplus.be.server.src.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointResponse {

    @Schema(description = "포인트 잔액", example = "10000")
    private Long pointBalance;
}
