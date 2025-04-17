package kr.hhplus.be.server.src.domain.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 상태")
public enum TokenStatus {

    READY("대기", "READY", 1),
    ACTIVE("활성화", "ACTIVE", 2),
    EXPIRED("만료", "EXPIRED", 3);

    private final String description;
    private final String code;
    private final int dbCode; //db값


    TokenStatus(String description, String code, int dbCode) {
        this.description = description;
        this.code = code;
        this.dbCode = dbCode;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public int getDbCode() {
        return dbCode;
    }

}
