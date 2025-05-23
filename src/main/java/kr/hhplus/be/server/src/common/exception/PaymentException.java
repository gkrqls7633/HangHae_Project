package kr.hhplus.be.server.src.common.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends RuntimeException {

    private final int status;

    public PaymentException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST.value(); // 기본 400
    }

    public PaymentException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
