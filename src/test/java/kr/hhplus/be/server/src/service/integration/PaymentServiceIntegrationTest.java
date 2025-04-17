package kr.hhplus.be.server.src.service.integration;


import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Payment;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.PaymentRepository;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.payment.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.PaymentResponse;
import kr.hhplus.be.server.src.service.PaymentService;
import kr.hhplus.be.server.src.service.testTransactionHelper.PaymentTransactionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentTransactionHelper paymentTransactionHelper;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = paymentTransactionHelper.setupTestData();
    }

    @DisplayName("결제 요청 처리 시 해당 예약내역의 좌석 상태 확인 및 유저 포인트 잔액 확인 후 결제 완료 처리한다.")
    @Test
    void processPaymentTest() {

        //given
        Long userId = paymentRequest.getUserId();
        Long bookingId = paymentRequest.getBookingId();

        //when
        ResponseMessage<PaymentResponse> response =  paymentService.processPayment(paymentRequest);

        // 유저 포인트 차감 처리 확인
        Point updatedPoint = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        //then
        //유저 포인트 차감 처리 확인
        assertEquals(50000L, updatedPoint.getPointBalance());

        //결제 상태 변경
        assertEquals(response.getData().getPaymentStatus(), PaymentStatus.COMPLETED);
    }
}
