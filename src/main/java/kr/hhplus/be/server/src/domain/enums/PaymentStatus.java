package kr.hhplus.be.server.src.domain.enums;

public enum PaymentStatus {
    PENDING("결제 대기", "PENDING"),
    COMPLETED("결제 완료", "COMPLETED"),
    FAILED("결제 실패", "FAILED"),
    CANCELED("결제 취소", "CANCELED"),
    REFUNDED("환불 완료", "REFUNDED");

    private final String description;
    private final String code;

    PaymentStatus(String description, String code) {
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
