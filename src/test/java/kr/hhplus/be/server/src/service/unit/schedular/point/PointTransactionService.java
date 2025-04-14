package kr.hhplus.be.server.src.service.unit.schedular.point;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;

@Service
public class PointTransactionService {

    @Autowired
    private PointRepository pointRepository;

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
        pointRepository.flush();
        System.out.println("###### [B] 포인트 수정 및 커밋 완료");
    }
}
