package kr.hhplus.be.server.src.service.integration;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;
import kr.hhplus.be.server.src.service.PointService;
import kr.hhplus.be.server.src.service.unit.point.PointTransactionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointTransactionHelper pointTransactionHelper;

    private Long savedUserId;


    @BeforeEach
    void setup() {
        savedUserId = pointTransactionHelper.setupTestData();
    }

    @DisplayName("포인트 충전 후 충전된 잔액이 정상 조회된다.")
    @Test
    void pointIntegrationTest() {
        // given
        PointChargeRequest request = new PointChargeRequest();
        request.setUserId(savedUserId);
        request.setChargePoint(50000L);

        // when
        pointService.chargePoint(request);

        // then
        ResponseMessage<PointResponse> response = pointService.getPoint(savedUserId);
        assertEquals(Optional.of(250000L).get(), response.getData().getPointBalance());
    }

}
