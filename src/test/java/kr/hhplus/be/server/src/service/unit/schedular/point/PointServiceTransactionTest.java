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
    private PointTransactionDirtyReadService pointTransactionDirtyReadService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    private Long savedUserId;

    @BeforeEach
    void setup() {
        // 1. 테스트용 유저 생성
        User user = new User();
        user.setUserName("김항해");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@naver.com");
        user.setAddress("서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);
        savedUserId = savedUser.getUserId();

        //2. 포인트 생성
        Point point = new Point();
        point.setUser(user);
        point.setPointBalance(200000L);
        pointRepository.save(point);

        pointRepository.flush();

    }

    @Test
    @DisplayName("Read Uncommitted : Dirty Read 테스트")
    @Commit
    public void pointServiceDirtyReadTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Thread transactionA = new Thread(() -> {
            try {
                pointTransactionDirtyReadService.transactionA(savedUserId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread transactionB = new Thread(() -> {
            try {
                latch.await();
                pointTransactionDirtyReadService.transactionB(savedUserId);
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
}
