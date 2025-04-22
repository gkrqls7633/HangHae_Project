package kr.hhplus.be.server.src.domain.point.unit;

import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class PointTransactionService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void readUncommitteTtransactionA(Long userId) throws InterruptedException {
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        point.setPointBalance(point.getPointBalance() + 5000L);
        pointRepository.save(point);

        System.out.println("###### 트랜잭션 A: 포인트 수정 완료(커밋 전)");
        Thread.sleep(2000);
        System.out.println("###### 트랜잭션 A 종료 (커밋)");

    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void readUncommitteTtransactionB(Long userId) {
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        System.out.println("###### 트랜잭션 B가 읽은 포인트 잔액: " + point.getPointBalance() + " ---> Dirty Read 발생!");
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readCommittedTransactionA(Long userId) throws InterruptedException {
        // 포인트 조회
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        point.setPointBalance(point.getPointBalance() + 5000L);  // 포인트 증가
        pointRepository.save(point);  // 커밋되지 않은 상태로 수정

        System.out.println("###### 트랜잭션 A: 포인트 수정 완료 (커밋 전)");
        // 2초 대기 후 커밋
        Thread.sleep(2000); // B가 이 사이에 읽도록 유도
        System.out.println("###### 트랜잭션 A 종료 (커밋)");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readCommittedTransactionB(Long userId) {
        // 트랜잭션 A에서 커밋된 데이터를 읽을 수 있는지 확인
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        System.out.println("###### 트랜잭션 B가 읽은 포인트 잔액: " + point.getPointBalance());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void repeatableReadTransactionA(Long userId, CountDownLatch latch) throws InterruptedException {
        Point point1 = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        System.out.println("###### [A] 첫 조회: " + point1.getPointBalance());
        latch.countDown(); // B 실행 시작 알림

        Thread.sleep(2000); // B가 중간에 수정하도록 대기

        Point point2 = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));
        System.out.println("###### [A] 두 번째 조회: " + point2.getPointBalance());

        if (point1.getPointBalance().equals(point2.getPointBalance())) {
            System.out.println("###### Repeatable Read 보장됨: 두 조회 결과 동일");
        } else {
            System.out.println("###### Repeatable Read 깨짐: 값이 달라짐");
        }
    }

    @Transactional
    public void repeatableReadTransactionB(Long userId) {
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        point.setPointBalance(point.getPointBalance() + 10000);
        pointRepository.save(point);
//        pointRepository.flush();
        System.out.println("###### [B] 포인트 수정 및 커밋 완료");
    }

    // 트랜잭션 A: 포인트 조회 후 대기
    // READ_COMMITTED는 Phantom Read 발생할 수 있음.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Point> readPointsGreaterThanEqual(Long amount) throws InterruptedException {
        // 조건에 맞는 포인트 조회
        List<Point> points = pointRepository.findByPointBalanceGreaterThanEqual(amount);
        System.out.println("🔒 [A] 첫 번째 조회 결과: " + points.size());

        // 잠시 대기하여 트랜잭션 B가 삽입할 시간을 줌
        Thread.sleep(2000);  // 2초 대기 후 다시 조회

        points = pointRepository.findByPointBalanceGreaterThanEqual(amount);  // 두 번째 조회
        System.out.println("🔒 [A] 두 번째 조회 결과: " + points.size());

        return points;
    }

    // 트랜잭션 B: 새로운 row 삽입
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertNewPoint(Long userId, Long pointBalance) {

        User user = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");

        User savedUser = userRepository.save(user);

        Point point = Point.of(savedUser.getUserId(), savedUser, 200000L);

        pointRepository.save(point);
        System.out.println("✏️ [B] 새로운 row 삽입 완료");
    }
}
