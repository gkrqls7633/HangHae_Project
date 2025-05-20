package kr.hhplus.be.server.src.common.exception;

import org.springframework.http.HttpStatus;

public class SeatException extends RuntimeException {

    private final int status;

    public SeatException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public SeatException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
