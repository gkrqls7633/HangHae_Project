package kr.hhplus.be.server.src.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.*;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.*;
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
    private final SeatRepository seatRepository;

    @Transactional
    public ResponseMessage<PaymentResponse> processPayment(PaymentRequest paymentRequest) {
        /*
        1. 예약 내역 조회
         - 예약번호 유효성 검증
         */
        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("예약 내역이 존재하지 않습니다."));

        Payment paymentDomain = Payment.builder()
                .userId(paymentRequest.getUserId())
                .bookingId(booking.getBookingId())
                .build();

        /*
        2. 결제 요청 유저와 예약된 유저 동일한지 체크
         */
        if (!paymentDomain.isBookingCheck(booking)) {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }

        /*
        3.. 결제할 콘서트의 예약 여부 체크 (좌석 점유 되어있는지)
         - Booking -> Seat -> seatStatus
         */
        Seat seat = seatRepository.findById(booking.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석이 존재하지 않습니다."));

        if (seat.getSeatStatus() == SeatStatus.BOOKED || seat.getSeatStatus() == SeatStatus.OCCUPIED) {
            throw new IllegalStateException("좌석 예약 내역을 재확인 해주세요.");
        }

        /*
         3. 포인트 잔액 조회
         - 포인트 잔액이 결제할 가격보다 많아야 함.
         - 포인트 및 콘서트 정보 조회
        */
        //예약 요청한 콘서트의 가격 정보 필요
        Concert concertInfo = concertRepository.findById(booking.getConcert().getConcertId())
                .orElseThrow(() -> new IllegalArgumentException("콘서트 정보가 없습니다."));

        Point point = pointRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 포인트 정보가 없습니다."));

        //포인트 부족 체크
        if (!point.isEnough(concertInfo.getPrice())) {
            throw new IllegalStateException("잔액을 확인해주세요.");
        }

        //결제 요청 -> 유저 잔액 포인트 차감
        Payment payment = paymentRepository.save(paymentDomain);

        //유저 잔액 차감
        point.usePoint(concertInfo.getPrice());
        pointRepository.save(point);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(payment.getPaymentId());
        paymentResponse.setBookingId(paymentRequest.getBookingId());
        paymentResponse.setPaymentStatus(PaymentStatus.COMPLETED);

        return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);
    }
}