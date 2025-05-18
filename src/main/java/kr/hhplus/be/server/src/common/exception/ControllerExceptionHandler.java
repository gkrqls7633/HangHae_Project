package kr.hhplus.be.server.src.common.exception;

import io.swagger.v3.oas.annotations.Hidden;
import kr.hhplus.be.server.src.common.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Hidden
@RestControllerAdvice(annotations = {RestController.class})
public class ControllerExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseMessage handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("# IllegalArgumentException : {}", e.getMessage());
        return ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseMessage handlerException(Exception e) {
        log.error("# handleException : ", e);
        return ResponseMessage.error( HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(BookingException.class)
    public ResponseMessage handleInvalidBookingException(BookingException e) {
        log.warn("# InvalidBookingException : {}", e.getMessage());
        return ResponseMessage.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseMessage handleInvalidPaymentException(PaymentException e) {
        log.warn("# InvalidPaymentException : {}", e.getMessage());
        return ResponseMessage.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(SeatException.class)
    public ResponseMessage handleInvalidPaymentException(SeatException e) {
        log.warn("# InvalidSeatException : {}", e.getMessage());
        return ResponseMessage.error(e.getStatus(), e.getMessage());
    }



}
