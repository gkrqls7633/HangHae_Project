package kr.hhplus.be.server.src.interfaces.queue.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QueueResponse {

    @Schema(description = "토큰 value", example = "1")
    private String tokenValue;

    @Schema(description = "토큰 상태", example = "1")
    private TokenStatus tokenStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "토큰 발급시간", example = "1")
    private LocalDateTime issuedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "토큰 만료시간", example = "1")
    private LocalDateTime expiredAt;

}
