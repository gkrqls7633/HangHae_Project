package kr.hhplus.be.server.src.interfaces.payment;

import lombok.Getter;

@Getter
public class PaymentRequest {

    private Long bookingId;

    private Long userId;

    private Long paymentPrice;

}
