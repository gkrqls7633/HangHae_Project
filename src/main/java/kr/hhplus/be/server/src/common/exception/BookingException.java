package kr.hhplus.be.server.src.common.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends RuntimeException {

    private final int status;

    public BookingException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST.value(); // 기본 400
    }

    public BookingException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
