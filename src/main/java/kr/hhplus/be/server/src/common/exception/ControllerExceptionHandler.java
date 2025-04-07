package kr.hhplus.be.server.src.common.exception;

import kr.hhplus.be.server.src.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseMessage handlerException(Exception e) {
        log.error("# handleException : ", e);
        return ResponseMessage.error( HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}
