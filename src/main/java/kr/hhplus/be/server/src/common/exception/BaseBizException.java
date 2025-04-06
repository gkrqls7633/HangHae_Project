package kr.hhplus.be.server.src.common.exception;

import lombok.Getter;

@Getter
public class BaseBizException extends RuntimeException {

    private static final long serialVersionUID = 5930650721134016714L;

    private int errorCode;
    private String errorMessage;
    private String returnMessage;
    private Object data;

    public BaseBizException() {

    }

    public BaseBizException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.returnMessage = errorMessage;
    }

    public <T> BaseBizException(String errorMessage, String returnMessage, T data) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.returnMessage = returnMessage;
        this.data = data;
    }


}
