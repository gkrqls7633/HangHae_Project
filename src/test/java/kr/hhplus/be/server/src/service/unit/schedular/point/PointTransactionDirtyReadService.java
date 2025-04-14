package kr.hhplus.be.server.src.service.unit.schedular.point;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointTransactionDirtyReadService {

    @Autowired
    private PointRepository pointRepository;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void transactionA(Long userId) throws InterruptedException {
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        point.setPointBalance(point.getPointBalance() + 5000L);
        pointRepository.save(point);

        System.out.println("######트랜잭션 A: 포인트 수정 완료(커밋 전)");
        Thread.sleep(2000);
        System.out.println("######트랜잭션 A 종료 (커밋)");

    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void transactionB(Long userId) {
        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));

        System.out.println("######트랜잭션 B가 읽은 포인트 잔액: " + point.getPointBalance() + "Dirty Read 발생!");
    }
}
