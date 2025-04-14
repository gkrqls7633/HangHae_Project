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
}
