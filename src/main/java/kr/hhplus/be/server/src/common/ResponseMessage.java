package kr.hhplus.be.server.src.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 객체")
public class ResponseMessage<T> {

    @Schema(description = "응답 코드", example = "200")
    private int status;

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    public ResponseMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseMessage<T> success(String message) {
        return new ResponseMessage<>(200, message, null);
    }

    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(200, "성공", data);
    }

    public static <T> ResponseMessage<T> success(String message, T data) {
        return new ResponseMessage<>(200, message, data);
    }

    public static <T> ResponseMessage<T> error(int status, String message) {
        return new ResponseMessage<>(status, message, null);
    }
}

