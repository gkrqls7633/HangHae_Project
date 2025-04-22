package kr.hhplus.be.server.src.domain.booking.integration;

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

    private BookingRequest bookingRequest;


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


    //동시성 이슈 -> 현재는 두 요청 모두 성공
    @DisplayName("동일 좌석 동시 예약 요청 시 하나만 성공해야 한다.")
    @Test
    void testConcurrencyBookingSeatTest() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //future로 비동기 작업 받을 수 있게 처리
        List<Future<ResponseMessage<BookingResponse>>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Future<ResponseMessage<BookingResponse>> result = executorService.submit(() -> {
                try {
                    return bookingService.bookingSeat(bookingRequest);
                } catch (Exception e) {
                    return ResponseMessage.error(500, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            results.add(result);
        }

        latch.await(); // 모든 스레드 완료까지 대기

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

        System.out.println("성공한 예약 요청 수: " + successCount);
        System.out.println("실패한 예약 요청 수: " + failCount);

        // 기대: 성공 1건
        assertEquals(1, successCount);
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
}
