package kr.hhplus.be.server.src.interfaces.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포인트 충전 요청 DTO")
public class PointChargeRequest {

    @Schema(description = "userId", example = "1")
    private Long userId;

    @Schema(description = "충전 포인트", example = "10000")
    private Long chargePoint;


}
