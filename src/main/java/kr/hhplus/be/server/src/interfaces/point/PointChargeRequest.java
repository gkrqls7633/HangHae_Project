package kr.hhplus.be.server.src.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "포인트 충전 요청 DTO")
public class PointChargeRequest {

    @Schema(description = "userId", example = "1")
    private Long userId;

    @Schema(description = "충전 포인트", example = "10000")
    private Long chargePoint;


}
