package kr.hhplus.be.server.src.domain.point.integration;

import kr.hhplus.be.server.src.TestcontainersConfiguration;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;
import kr.hhplus.be.server.src.domain.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

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

    @DisplayName("동일 유저에 대해 동시 포인트 충전 요청 시 하나만 성공해야 한다.")
    @Test
    void testConcurrencyChargePointTest() throws InterruptedException {
        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PointChargeRequest pointChargeRequest = new PointChargeRequest(savedUserId, 100000L);

        List<Future<ResponseMessage<PointResponse>>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Future<ResponseMessage<PointResponse>> result = executorService.submit(() -> {
                try {
                    return pointService.chargePoint(pointChargeRequest);
                } catch (Exception e) {
                    return ResponseMessage.error(500, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            results.add(result);
        }

        latch.await();

        long successCount = results.stream()
                .filter(future -> {
                    try {
                        return future.get().getStatus() == 200;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        long failCount = results.stream()
                .filter(future -> {
                    try {
                        return future.get().getStatus() != 200;
                    } catch (Exception e) {
                        return true;
                    }
                })
                .count();

        System.out.println("성공한 포인트 충전 요청 수: " + successCount);
        System.out.println("실패한 포인트 충전 요청 수: " + failCount);

        // 기대: 충전 성공 1건, 실패 2건
        assertEquals(1, successCount);  // 중복 충전이 발생하지 않도록 1번만 성공해야 함
    }

}
