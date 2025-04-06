package kr.hhplus.be.server.src.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 요청 DTO")
public class PointChargeRequest {

    public PointChargeRequest() {

    }

    @Schema(description = "userId", example = "1")
    private String userId;

    @Schema(description = "충전 포인트", example = "10000")
    private Long chargePoint;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getChargePoint() {
        return chargePoint;
    }

    public void setChargePoint(Long chargePoint) {
        this.chargePoint = chargePoint;
    }
}
