package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 점유 상태")
public enum SeatStatus {

    AVAILABLE("예약 가능", "AVAILABLE"),
    BOOKED("예약 완료", "BOOKED"),
    OCCUPIED("점유 중", "OCCUPIED");

    private final String description;
    private final String code;

    SeatStatus(String description, String code) {
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
