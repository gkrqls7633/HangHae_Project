package kr.hhplus.be.server.src.common.exception;

import org.springframework.http.HttpStatus;

public class PointException extends RuntimeException {
    private final int status;

    public PointException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public PointException(String message, int status) {
        super(message);
        this.status = status;
    }

    public PointException(String message, Object... args) {
        super(String.format(message, args));
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public int getStatus() {
        return status;
    }
}
