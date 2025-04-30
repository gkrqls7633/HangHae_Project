package kr.hhplus.be.server.src.domain.booking.integration;

import kr.hhplus.be.server.src.application.service.BookingServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingResponse;
import kr.hhplus.be.server.src.domain.booking.BookingService;
import kr.hhplus.be.server.src.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
//    @Test
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
//    @Test
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

    @DisplayName("좌석 예약 레디스 분산락 적용 테스트")
    @Test
    void bookingSeatWithRedisLock() throws InterruptedException {

        //given
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Long concertId = bookingRequest.getConcertId();
        Long seatNum = bookingRequest.getSeatNum();

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 락을 대기하는 로그 및 예약 요청
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " -- 락 대기 중 --");
                    bookingService.bookingSeat(bookingRequest);
                    successCount.addAndGet(1);
                    System.out.println(Thread.currentThread().getName() + " -- 예약 성공 --");

                } catch (Exception e) {
                    failCount.addAndGet(1);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // when & then
        Optional<Seat> seatOpt = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(concertId, seatNum);
        assertTrue("좌석이 존재해야 합니다.", seatOpt.isPresent());
        Seat seat = seatOpt.get();
        assertEquals("좌석 상태는 OCCUPIED여야 합니다.", seat.getSeatStatus(), SeatStatus.OCCUPIED);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);

    }

}
