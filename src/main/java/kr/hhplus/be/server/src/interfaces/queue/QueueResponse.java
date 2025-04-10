package kr.hhplus.be.server.src.interfaces.queue;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
public class QueueResponse {

    @Schema(description = "토큰 value", example = "1")
    private String tokenValue;

    @Schema(description = "토큰 상태", example = "1")
    private TokenStatus tokenStatus;


    @Schema(description = "토큰 발급시간", example = "1")
    private LocalDateTime issuedAt;

    @Schema(description = "토큰 만료시간", example = "1")
    private LocalDateTime expiredAt;

    @Schema(description = "예약id", example = "1")
    private Long bookId;

}
