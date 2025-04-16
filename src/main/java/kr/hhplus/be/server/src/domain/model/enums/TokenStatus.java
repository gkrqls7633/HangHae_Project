package kr.hhplus.be.server.src.domain.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 상태")
public enum TokenStatus {

    ACTIVE("활성화", "ACTIVE"),
    EXPIRED("만료", "EXPIRED"),
    READY("대기", "READY");

    private final String description;
    private final String code;

    TokenStatus(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
