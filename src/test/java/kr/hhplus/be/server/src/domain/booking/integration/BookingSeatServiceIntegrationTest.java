package kr.hhplus.be.server.src.domain.booking.integration;

import kr.hhplus.be.server.src.application.service.BookingServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingResponse;
import kr.hhplus.be.server.src.domain.booking.BookingService;
import kr.hhplus.be.server.src.TestcontainersConfiguration;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class BookingSeatServiceIntegrationTest {

    @Autowired
    private BookingTransactionHelper bookingTransactionHelper;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RedissonClient redissonClient;

    private BookingRequest bookingRequest;
    @Autowired
    private BookingServiceImpl bookingServiceImpl;


    @BeforeEach
    void setup() {
        bookingRequest = bookingTransactionHelper.setupTestData();
    }

    @DisplayName("좌석 예약 요청 시 콘서트id, 좌석num, userId 기반으로 해당 좌석 점유 및 예약상태가 된다.")
    @Test
    void bookingSeatIntegrationTest() {

        //given
        Long concertId = bookingRequest.getConcertId();
//        Long userId = bookingRequest.getUserId();
        Long seatNum = bookingRequest.getSeatNum();

        //when
        bookingService.bookingSeat(bookingRequest);

        //then : 해당 좌석이 점유상태로 변경됐는지 조회
        Optional<Seat> seatOpt = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(concertId, seatNum);
        assertTrue("좌석이 존재해야 합니다.", seatOpt.isPresent());
        Seat seat = seatOpt.get();
        assertEquals(seat.getSeatStatus(), SeatStatus.OCCUPIED);
    }

    @DisplayName("동시 좌석 예약 시 하나만 성공한다.(낙관적 락)")
    @Test
    void bookingSeatOptimisticLockTest() throws InterruptedException {
        User user1 = bookingTransactionHelper.createUser(1L);
        User user2 = bookingTransactionHelper.createUser(2L);

        Queue queue1 = bookingTransactionHelper.createQueue(user1);
        Queue queue2 = bookingTransactionHelper.createQueue(user2);

        BookingRequest request1 = new BookingRequest(bookingRequest.getConcertId(), 1L, user1.getUserId());
        BookingRequest request2 = new BookingRequest(bookingRequest.getConcertId(), 1L, user2.getUserId());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<ResponseMessage<BookingResponse>>> futures = new ArrayList<>();

        futures.add(executor.submit(() -> bookingService.bookingSeat(request1)));
        futures.add(executor.submit(() -> bookingService.bookingSeat(request2)));

        long startTime = System.currentTimeMillis();

        int successCount = 0;
        int failCount = 0;

        for (Future<ResponseMessage<BookingResponse>> future : futures) {
            try {
                ResponseMessage<BookingResponse> response = future.get();
                if (response.getStatus() == 200) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ObjectOptimisticLockingFailureException) {
                    failCount++;
                } else {
                    throw new RuntimeException("Unexpected exception", cause);
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("성공한 스레드 수: " + successCount);
        System.out.println("실패한 스레드 수: " + failCount);
        System.out.println("총 소요 시간: " + duration + "ms");


        assertEquals(1, successCount);
        assertEquals(1, failCount);
    }

    @DisplayName("동시 좌석 예약 시 하나만 성공한다.(비관적 락)")
    @Test
    void bookingSeatPessimisticLockTest() throws InterruptedException {
        User user1 = bookingTransactionHelper.createUser(1L);
        User user2 = bookingTransactionHelper.createUser(2L);

        Queue queue1 = bookingTransactionHelper.createQueue(user1);
        Queue queue2 = bookingTransactionHelper.createQueue(user2);

        BookingRequest request1 = new BookingRequest(bookingRequest.getConcertId(), 1L, user1.getUserId());
        BookingRequest request2 = new BookingRequest(bookingRequest.getConcertId(), 1L, user2.getUserId());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<ResponseMessage<BookingResponse>>> futures = new ArrayList<>();

        futures.add(executor.submit(() -> bookingService.bookingSeatWithPessimisticLock(request1)));
        futures.add(executor.submit(() -> bookingService.bookingSeatWithPessimisticLock(request2)));

        long startTime = System.currentTimeMillis();

        int successCount = 0;
        int failCount = 0;

        for (Future<ResponseMessage<BookingResponse>> future : futures) {
            try {
                ResponseMessage<BookingResponse> response = future.get();
                if (response.getStatus() == 200) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ObjectOptimisticLockingFailureException) {
                    failCount++;
                } else {
                    throw new RuntimeException("Unexpected exception", cause);
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("성공한 스레드 수: " + successCount);
        System.out.println("실패한 스레드 수: " + failCount);
        System.out.println("총 소요 시간: " + duration + "ms");


        assertEquals(1, successCount);
        assertEquals(1, failCount);
    }

    @Test
    void 좌석예약_분산락_적용_테스트() throws InterruptedException {

        //given
        Long concertId = bookingRequest.getConcertId();
        Long seatNum = bookingRequest.getSeatNum();

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 락을 대기하는 로그 및 예약 요청
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 로그: 락을 기다리고 있다는 표시
                    System.out.println(Thread.currentThread().getName() + " - 락 대기 중...");

                    // 분산락 적용 메서드 호출
                    bookingService.bookingSeat(bookingRequest);

                    // 예약이 성공적으로 이루어진 후
                    System.out.println(Thread.currentThread().getName() + " - 예약 성공");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // then
        // 1. 해당 좌석의 상태가 OCCUPIED로 변경되었는지 확인
        Optional<Seat> seatOpt = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(concertId, seatNum);
        assertTrue("좌석이 존재해야 합니다.", seatOpt.isPresent());
        Seat seat = seatOpt.get();
        assertEquals("좌석 상태는 OCCUPIED여야 합니다.", seat.getSeatStatus(), SeatStatus.OCCUPIED);

    }


//    @DisplayName("좌석 예약 요청 시 콘서트 id, 좌석 num, userId 기반으로 해당 좌석 점유 및 예약상태가 된다.")
//    @Test
//    void bookingSeatIntegrationWithRedisLockTest() throws InterruptedException {
//
//        // given: 테스트용 데이터 준비
//        Long concertId = bookingRequest.getConcertId();
//        Long seatNum = bookingRequest.getSeatNum();
//
//        System.out.println("테스트 시작: 콘서트 ID = " + concertId + ", 좌석 번호 = " + seatNum);
//
//        // first thread: 첫 번째 예약을 시도
//        Thread firstThread = new Thread(() -> {
//            try {
//                System.out.println("첫 번째 예약 시도 시작: 콘서트 ID = " + concertId + ", 좌석 번호 = " + seatNum);
//
//                // first booking request
//                ResponseMessage<BookingResponse> response = bookingService.bookingSeat(bookingRequest);
//                System.out.println("첫 번째 예약 시도 완료: 콘서트 ID = " + concertId + ", 좌석 번호 = " + seatNum);
//
//                assertEquals(200, response.getStatus());
//            } catch (Exception e) {
//                System.err.println("첫 번째 예약 시도 중 오류 발생: " + e.getMessage());
//            }
//        });
//
//        // second thread: 두 번째 예약을 시도
//        Thread secondThread = new Thread(() -> {
//            try {
//                // second booking request (이 예약은 실패해야 합니다, 락이 이미 걸려있기 때문에)
//                System.out.println("두 번째 예약 시도 시작: 콘서트 ID = " + concertId + ", 좌석 번호 = " + seatNum);
//
//                ResponseMessage<BookingResponse> response = bookingService.bookingSeat(bookingRequest);
//                System.out.println("두 번째 예약 시도 완료: 콘서트 ID = " + concertId + ", 좌석 번호 = " + seatNum);
//
//                assertNotEquals(200, response.getStatus());
//            } catch (Exception e) {
//                System.err.println("두 번째 예약 시도 중 오류 발생: " + e.getMessage());
//            }
//        });
//
//        // when: 첫 번째 예약 스레드 실행
//        firstThread.start();
//        // 잠시 대기: 두 번째 스레드가 첫 번째 스레드가 락을 걸고 기다리게 함
//        Thread.sleep(100);
//
//        // 두 번째 예약 스레드 실행
//        secondThread.start();
//
//        // 두 스레드 모두 실행 완료될 때까지 기다립니다.
//        firstThread.join();
//        secondThread.join();
//
//        // then: 해당 좌석이 점유상태로 변경됐는지 확인
//        Optional<Seat> seatOpt = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(concertId, seatNum);
//        assertTrue("좌석이 존재해야 합니다.", seatOpt.isPresent());
//        Seat seat = seatOpt.get();
//        System.out.println("예약 후 좌석 상태: " + seat.getSeatStatus());
//
//        assertEquals("좌석 상태는 OCCUPIED여야 합니다.", seat.getSeatStatus(), SeatStatus.OCCUPIED);
//
//        // 락이 풀렸는지 확인
//        RLock rLock = redissonClient.getLock("LOCK:" + concertId + ":" + seatNum);
//        System.out.println("락 상태 확인: " + (rLock.isLocked() ? "락이 걸려 있음" : "락이 풀림"));
//
//        assertFalse(rLock.isLocked(), "트랜잭션이 끝난 후 락은 해제되어야 합니다.");
//    }

}
