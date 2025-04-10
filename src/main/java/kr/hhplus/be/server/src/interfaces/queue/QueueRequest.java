package kr.hhplus.be.server.src.interfaces.queue;

// 대기열 진입 위한 req DTO

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "대기열 관련 요청 DTO")
public class QueueRequest {

    @Schema(description = "userId", example = "1")
    private Long userId;

    @Schema(description = "bookingId", example = "1")
    private Long bookingId;
}
