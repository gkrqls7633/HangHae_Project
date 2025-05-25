package kr.hhplus.be.server.src.interfaces.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponse {

    @Schema(description = "포인트 잔액", example = "10000")
    private Long pointBalance;
}
