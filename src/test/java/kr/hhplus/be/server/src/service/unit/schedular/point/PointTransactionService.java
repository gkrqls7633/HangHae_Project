package kr.hhplus.be.server.src.service.unit.schedular.point;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
}
