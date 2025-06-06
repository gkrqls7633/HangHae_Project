package kr.hhplus.be.server.src.domain.point.integration;

import kr.hhplus.be.server.src.TestcontainersConfiguration;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.infra.persistence.point.PointRepositoryImpl;
import kr.hhplus.be.server.src.interfaces.api.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.api.point.dto.PointResponse;
import kr.hhplus.be.server.src.domain.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    private PointRepositoryImpl pointRepositoryImpl;

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

//    @DisplayName("동일 유저에 대해 동시 포인트 충전 요청 시 하나만 성공해야 한다.(락 x)")
//    @Test
//    void testConcurrencyChargePointTest() throws InterruptedException {
//        int threadCount = 3;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        PointChargeRequest pointChargeRequest = new PointChargeRequest(savedUserId, 100000L);
//
//        List<Future<ResponseMessage<PointResponse>>> results = new ArrayList<>();
//
//        for (int i = 0; i < threadCount; i++) {
//            Future<ResponseMessage<PointResponse>> result = executorService.submit(() -> {
//                try {
//                    return pointService.chargePoint(pointChargeRequest);
//                } catch (Exception e) {
//                    return ResponseMessage.error(500, e.getMessage());
//                } finally {
//                    latch.countDown();
//                }
//            });
//            results.add(result);
//        }
//
//        latch.await();
//
//        long successCount = results.stream()
//                .filter(future -> {
//                    try {
//                        return future.get().getStatus() == 200;
//                    } catch (Exception e) {
//                        return false;
//                    }
//                })
//                .count();
//
//        long failCount = results.stream()
//                .filter(future -> {
//                    try {
//                        return future.get().getStatus() != 200;
//                    } catch (Exception e) {
//                        return true;
//                    }
//                })
//                .count();
//
//        System.out.println("성공한 포인트 충전 요청 수: " + successCount);
//        System.out.println("실패한 포인트 충전 요청 수: " + failCount);
//
//        // 기대: 충전 성공 1건, 실패 2건
//        assertEquals(1, successCount);  // 중복 충전이 발생하지 않도록 1번만 성공해야 함
//    }

    @DisplayName("동일 유저에 대해 동시 포인트 충전 요청 시 하나만 성공해야 한다.(낙관적 락 반영)")
    @Test
    void optimisticLockTest() throws InterruptedException {
        //given
        PointChargeRequest pointChargeRequest = new PointChargeRequest(savedUserId, 100000L);

        // 스레드 수
        int threadCount = 2;
        List<Thread> threads = new ArrayList<>();
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호
        CountDownLatch doneLatch = new CountDownLatch(threadCount); // 완료 대기

        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                try {
                    startLatch.await(); // 모두가 동시에 시작 대기

                    ResponseMessage<PointResponse> result = pointService.chargePointWithLock(pointChargeRequest);
                    if (result.getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (ObjectOptimisticLockingFailureException e) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        // 스레드 시작
        for (Thread thread : threads) {
            thread.start();
        }

        startLatch.countDown();
        doneLatch.await();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("성공한 스레드 수: " + successCount.get());
        System.out.println("실패한 스레드 수: " + failureCount.get());
        System.out.println("총 소요 시간: " + duration + "ms");

        //then : 성공은 1
        assertEquals(1, successCount.get());
    }

    //Point에 version으로 낙관적락 걸어둔 상태라서 version에 의해 비관적락 적용 안됨.
    @DisplayName("동일 유저에 대해 동시 포인트 충전 요청 시 하나만 성공해야 한다.(비관적 락 반영)")
//    @Test
    void pessimiticLockTest() throws InterruptedException {
        //given
        PointChargeRequest pointChargeRequest = new PointChargeRequest(savedUserId, 100000L);

        // 스레드 수
        int threadCount = 3;
        List<Thread> threads = new ArrayList<>();
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        //when
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                try {
                    ResponseMessage<PointResponse> result = pointService.chargePointWithPessimisticLock(pointChargeRequest);
                    if (result.getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (ObjectOptimisticLockingFailureException | IllegalStateException e) {
                    failureCount.incrementAndGet();
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("성공한 스레드 수: " + successCount.get());
        System.out.println("실패한 스레드 수: " + failureCount.get());
        System.out.println("총 소요 시간: " + duration + "ms");

        //then : 성공은 1
        assertEquals(1, successCount.get());
    }

    @DisplayName("포인트 충전 레디스 분산락 적용 테스트")
    @Test
    void chargePointWithRedisLock() throws InterruptedException {

        // given
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        PointChargeRequest request = new PointChargeRequest();
        request.setUserId(savedUserId);
        request.setChargePoint(50000L);

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 락을 대기하는 로그 및 예약 요청
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " -- 락 대기 중 --");
                    pointService.chargePoint(request);
                    successCount.addAndGet(1);
                    System.out.println(Thread.currentThread().getName() + " -- 충전 성공 --");

                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.addAndGet(1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        ResponseMessage<PointResponse> response = pointService.getPoint(savedUserId);
        assertEquals(Optional.of(250000L).get(), response.getData().getPointBalance());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);
    }

}
