package kr.hhplus.be.server.src.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.Payment;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.domain.repository.PaymentRepository;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final ConcertRepository concertRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public ResponseMessage<PaymentResponse> processPayment(PaymentRequest paymentRequest) {

        PaymentResponse paymentResponse = new PaymentResponse();

        /*
        1. 예약 내역 조회
         - 결제할 콘서트의 예약 여부 체크 (좌석 점유 되어있는지)
         - Payment -> Booking
         */
        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("예약 내역이 존재하지 않습니다."));

        Payment paymentDomain = Payment.builder()
                .userId(paymentRequest.getUserId())
                .booking(booking)
                .build();

        /*
        2. bookingId -> 결제할 콘서트 정보 조회 (가격)
         - 결제할 콘서트의 예약 여부 체크 (좌석 점유 되어있는지)
         - Payment -> Booking
         */
        if (!paymentDomain.isBookingCheck()) {
            throw new IllegalStateException("좌석 예약 내역을 재확인 해주세요.");
        }

        /*
         3. 포인트 잔액 조회
         - 포인트 잔액이 결제할 가격보다 많아야 함.
         - 포인트 및 콘서트 정보 조회
        */
        Point point = pointRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 포인트 정보가 없습니다."));

        Concert concertInfo = concertRepository.findById(booking.getConcert().getConcertId())
                .orElseThrow(() -> new IllegalArgumentException("콘서트 정보가 없습니다."));

        //포인트 부족 체크
        if (!point.isEnough(concertInfo.getPrice())) {
            throw new IllegalStateException("잔액을 확인해주세요.");
        }

        //결제 요청 -> 유저 잔액 포인트 차감
        Payment payment = paymentRepository.save(paymentDomain);

        paymentResponse.setPaymentId(payment.getPaymentId());
        paymentResponse.setBookingId(paymentRequest.getBookingId());
        paymentResponse.setPaymentStatus(PaymentStatus.COMPLETED);

        //유저 잔액 차감
        point.usePoint(concertInfo.getPrice());
        pointRepository.save(point);

        return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);
    }
}
