package kr.hhplus.be.server.src.interfaces.queue;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "대기열 만료 관련 요청 DTO")
public class QueueExpireRequest {

    @Schema(description = "대기열 ID", example = "1", required = false)
    private Long queueId;

    @Schema(description = "토큰 값", example = "abc123token", required = false)
    private String tokenValue;

    @Schema(description = "토큰 발급 시간", example = "2025-04-10T15:30:00", required = false)
    private LocalDateTime issuedAt;

    @Schema(description = "토큰 만료 시간", example = "2025-04-11T15:30:00", required = false)
    private LocalDateTime expiredAt;

    @Schema(description = "토큰 상태", example = "ACTIVE", required = false)
    private TokenStatus tokenStatus;

    @Schema(description = "유저 ID", example = "123", required = true)
    private Long userId;

    public QueueExpireRequest(Long queueId, String tokenValue, LocalDateTime issuedAt, LocalDateTime expiredAt, TokenStatus tokenStatus) {
        this.queueId = queueId;
        this.tokenValue = tokenValue;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.tokenStatus = tokenStatus;
    }
}
