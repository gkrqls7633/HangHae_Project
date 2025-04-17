package kr.hhplus.be.server.src.domain.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 점유 상태")
public enum SeatStatus {

    AVAILABLE("예약 가능", "AVAILABLE",1),
    OCCUPIED("점유 중", "OCCUPIED",2),
    BOOKED("예약 완료", "BOOKED",3);

    private final String description;
    private final String code;
    private final int dbCode;

    SeatStatus(String description, String code, int dbCode) {
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
