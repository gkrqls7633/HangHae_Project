package kr.hhplus.be.server.src.application;


import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.booking.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.PaymentResponse;
import kr.hhplus.be.server.src.service.BookingService;
import kr.hhplus.be.server.src.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookingPaymentFacade {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    public ResponseMessage<PaymentResponse> processBookingPayment(BookingRequest bookingRequest){

        // 1. booking process
        ResponseMessage<BookingResponse> bookingResponse = bookingService.bookingSeat(bookingRequest);


        //todo : 결제 호출 구현
        //2. payment process
        if (bookingResponse.getStatus() == 200) { // 예약 성공
            PaymentRequest paymentRequest = new PaymentRequest();

            return paymentService.processPayment(paymentRequest);
        }

        return ResponseMessage.error(500, "예약 결제가 실패했습니다.");
    }


}
