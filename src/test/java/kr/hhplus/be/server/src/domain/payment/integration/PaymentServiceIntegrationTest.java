package kr.hhplus.be.server.src.domain.payment.integration;


import kr.hhplus.be.server.src.TestcontainersConfiguration;
import kr.hhplus.be.server.src.application.service.PaymentServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.payment.PaymentRepository;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.src.interfaces.payment.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentTransactionHelper paymentTransactionHelper;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {

        paymentTransactionHelper.cleanTestData();
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
