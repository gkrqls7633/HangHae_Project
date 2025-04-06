package kr.hhplus.be.server.src.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionMessage {

    @Schema(example = "API 버전")
    private String apiVersion;
    @Schema(example = "에러 상태 코드")
    private Integer statusCode;
    @Schema(example = "에러 응답 코드")
    private String resultCode;
    @Schema(example = "에러 응답 메시지")
    private String resultMessage;
}
