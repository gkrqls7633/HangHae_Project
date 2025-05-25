package kr.hhplus.be.server.src.domain.payment;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.api.payment.dto.PaymentResponse;

public interface PaymentService {

    ResponseMessage<PaymentResponse> processPayment(PaymentRequest paymentRequest);
    

}