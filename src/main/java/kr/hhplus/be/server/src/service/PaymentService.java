package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Payment;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.repository.PaymentRepository;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private static final String mockYsno = "Y";

    public final PaymentRepository paymentRepository;
    public final PointRepository pointRepository;

    public ResponseMessage<PaymentResponse> processPayment(PaymentRequest paymentRequest) {

        PaymentResponse paymentResponse = new PaymentResponse();

        Payment payment = Payment.builder()
                .userId(paymentRequest.getUserId())
                .build();

        // 1. bookingId -> 결제할 콘서트 정보 조회 (가격)
        // - 결제할 콘서트의 예약 여부 체크 (좌석 점유 되어있는지)
        // Payment -> Booking

        if (!payment.isBookingCheck()) {
            return ResponseMessage.error(500, "좌석 예약 내역을 재확인 해주세요.");
        }

        // 2. 포인트 잔액 조회
        // - 포인트 잔액이 결제할 가격보다 많아야 함.

//        Optional<Point> point = pointRepository.findById(paymentRequest.getUserId());
//        if (point.get().isEnough()) {
//
//        }


        // 마지막으로 결제 요청
        Payment paymentRes = paymentRepository.save(payment);

        paymentResponse.setPaymentId(paymentRes.getPaymentId());
        paymentResponse.setBookingId(paymentRequest.getBookingId());
        paymentResponse.setPaymentStatus(PaymentStatus.COMPLETED);

        return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);
    }
}
