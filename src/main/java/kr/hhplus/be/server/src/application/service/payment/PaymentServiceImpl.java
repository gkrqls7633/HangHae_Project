package kr.hhplus.be.server.src.application.service.payment;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.application.service.payment.event.publisher.PaymentEventPublisher;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.common.exception.PaymentException;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import kr.hhplus.be.server.src.domain.external.ExternalDataSaveEvent;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.payment.Payment;
import kr.hhplus.be.server.src.domain.payment.PaymentRepository;
import kr.hhplus.be.server.src.domain.payment.PaymentService;
import kr.hhplus.be.server.src.domain.payment.event.SeatBookedCompletedEvent;
import kr.hhplus.be.server.src.domain.payment.event.UserPointUsedEvent;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final ConcertRepository concertRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
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
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        //결제 대기 상태 저장
        paymentRepository.save(paymentDomain);

        try {
            /*
            2. 결제 요청 유저와 예약된 유저 동일한지 유효성 체크
             */
            if (!paymentDomain.isBookingCheck(booking)) {
                paymentDomain.changePaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(paymentDomain);
                throw new PaymentException("유저 정보가 일치하지 않습니다.");
            }

            /*
            3.. 결제할 콘서트의 예약 여부 체크 (좌석 점유 되어있는지)
             - Booking -> Seat -> seatStatus
             */
            if (booking.getSeatId() == null) {
                throw new PaymentException("예약 정보의 좌석 ID값이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            Seat seat = seatRepository.findById(booking.getSeatId())
                    .orElseThrow(() -> new PaymentException("해당 좌석이 존재하지 않습니다."));

            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                paymentDomain.changePaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(paymentDomain);
                throw new PaymentException("좌석 예약 내역을 재확인 해주세요.");
            }

            /*
             4. 포인트 잔액 조회
             - 포인트 잔액이 결제할 가격보다 많아야 함.
             - 포인트 및 콘서트 정보 조회
            */
            //예약 요청한 콘서트의 가격 정보 필요
            Concert concertInfo = concertRepository.findById(booking.getConcert().getConcertId())
                    .orElseThrow(() -> new PaymentException("콘서트 정보가 없습니다."));

            Point point = pointRepository.findById(paymentRequest.getUserId())
                    .orElseThrow(() -> new PaymentException("사용자 포인트 정보가 없습니다."));

            //포인트 부족 체크
            if (!point.isEnough(concertInfo.getPrice())) {
                paymentDomain.changePaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(paymentDomain);
                throw new PaymentException("잔액을 확인해주세요.");
            }

            //결제 요청 -> 결제 완료 상태 변경
            paymentDomain.changePaymentStatus(PaymentStatus.COMPLETED);
            Payment payment = paymentRepository.save(paymentDomain);

            /*
             유저 잔액 차감 이벤트 발행
            */
            paymentEventPublisher.success(new UserPointUsedEvent(point, concertInfo.getPrice()));

            /*
             좌석 상태 변경 이벤트 발행 (occupied -> Booked(예약 완료된 좌석))
            */
            paymentEventPublisher.success(new SeatBookedCompletedEvent(seat));

            /*
             외부 데이터 플랫폼 저장 이벤트 발행
             */
            paymentEventPublisher.success(new ExternalDataSaveEvent(payment));

            PaymentResponse paymentResponse = PaymentResponse.of(
                      payment.getPaymentId()
                    , paymentRequest.getBookingId()
                    , PaymentStatus.COMPLETED
            );

            return ResponseMessage.success("결제가 완료됐습니다.", paymentResponse);

        } catch (RuntimeException e) {
            log.error("결제 실패 - {}", e.getMessage());
            throw e;
        }
    }
}
