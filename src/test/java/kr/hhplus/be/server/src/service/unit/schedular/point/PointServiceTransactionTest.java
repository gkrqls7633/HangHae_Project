package kr.hhplus.be.server.src.service.unit.schedular.point;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PointServiceTransactionTest {

    @Autowired
    private PointTransactionService pointTransactionService;

    @Autowired
    private PointTransactionHelper pointTransactionHelper;

    @Autowired
    private PointRepository pointRepository;

    private Long savedUserId;

    @BeforeEach
    void setup() {
        savedUserId = pointTransactionHelper.setupTestData();
    }

    @Test
    @DisplayName("Read Uncommitted : Dirty Read 테스트")
    @Commit
    public void pointServiceDirtyReadTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Thread transactionA = new Thread(() -> {
            try {
                pointTransactionService.readUncommitteTtransactionA(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread transactionB = new Thread(() -> {
            try {
                latch.await();
                pointTransactionService .readUncommitteTtransactionB(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        transactionA.start();
        Thread.sleep(100);
        latch.countDown();
        transactionB.start();

        transactionA.join();
        transactionB.join();
    }

    @Test
    @DisplayName("Read Committed : Dirty Read 방지 테스트")
    @Commit
    public void pointServiceReadCommittedTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Point point = pointRepository.findById(savedUserId).get();
        System.out.println(point.getPointBalance());

        // 트랜잭션 A: 포인트 수정 후 커밋되지 않은 상태로 대기
        Thread transactionA = new Thread(() -> {
            try {
                pointTransactionService.readCommittedTransactionA(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 트랜잭션 B: 트랜잭션 A가 커밋된 후 데이터를 읽을 수 있도록 대기
        Thread transactionB = new Thread(() -> {
            try {
                latch.await();
                pointTransactionService.readCommittedTransactionB(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        transactionA.start();
        Thread.sleep(100);
        latch.countDown();  // 트랜잭션 B 시작
        transactionB.start();

        transactionA.join();
        transactionB.join();
    }

    @Test
    @DisplayName("Repeatable Read : 동일 트랜잭션 내 동일 조회 결과 보장 여부 테스트")
    @Commit
    public void pointServiceRepeatableReadTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Thread transactionA = new Thread(() -> {
            try {
                pointTransactionService.repeatableReadTransactionA(savedUserId, latch);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread transactionB = new Thread(() -> {
            try {
                latch.await(); // A가 첫 조회한 후 실행
                pointTransactionService.repeatableReadTransactionB(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        transactionA.start();
        Thread.sleep(500); // B가 A의 첫 조회 이후 실행되도록 유도
        transactionB.start();

        transactionA.join();
        transactionB.join();
        }
}
