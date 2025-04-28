package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.point.PointService;
import kr.hhplus.be.server.src.infra.lock.DistributedLock;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    private static final int MAX_RETRY = 3;

    /**
     * @description 유저의 포인트 잔액을 조회한다.
     * @param userId
     * @return 포인트 잔액
     */
    @Override
    @Transactional
    public ResponseMessage<PointResponse> getPoint(Long userId) {

        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보가 없습니다."));

        Long userPointBalance = point.getPointBalance();

        PointResponse pointResponse = PointResponse.builder()
                .pointBalance(userPointBalance)
                .build();

        return ResponseMessage.success("포인트 잔액이 정상적으로 조회됐습니다.", pointResponse);
    }

    /**
     * @description 유저의 포인트를 충전한다.
     * @param pointChargeRequest
     * @return
     */
    /*
    1. 락 키 설정 : userId
    2. 락 범위 : 유저 ID로 락 범위 지정
    3. 중복 처리 가능 여부 : x => LockWaitime : 0초로 지정
    */
    @Override
    @DistributedLock(
            key = "'charge:' + #pointChargeRequest.userId",
            waitTime = 0
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseMessage<PointResponse> chargePoint(PointChargeRequest pointChargeRequest) {

        //현재 잔액 조회
        Point point = pointRepository.findById(pointChargeRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보가 없습니다."));

        if (point.isCharged()) {
            throw new IllegalStateException("이미 충전된 요청입니다.");
        }

        //충전 호출
        point.chargePoint(pointChargeRequest);

        //충전 저장
        pointRepository.save(point);

        point.setCharged(true);

        PointResponse pointResponse = PointResponse.builder()
                .pointBalance(point.getPointBalance())
                .build();

        return ResponseMessage.success("포인트가 정상적으로 충전됐습니다.", pointResponse);
    }






    //낙관적 락 반영 메서드
    @Override
    public ResponseMessage<PointResponse> chargePointWithLock(PointChargeRequest pointChargeRequest) {
        try {
            return chargePoint(pointChargeRequest);

        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("충전 실패: Optimistic Lock 예외 발생", e);
            throw e;
        }
    }

    @Transactional
    public ResponseMessage<PointResponse> chargePointWithPessimisticLock(PointChargeRequest pointChargeRequest) {

        // 비관적 락을 사용하여 현재 포인트 정보 가져오기
        Point point = pointRepository.findByUserIdForUpdate(pointChargeRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보가 없습니다."));

        // 이미 충전된 요청이라면 예외를 던지기
        if (point.isCharged()) { // 충전 상태를 체크하는 로직
            throw new IllegalStateException("이미 충전된 요청입니다.");
        }

        // 충전 호출
        point.chargePoint(pointChargeRequest);

        // 충전 상태를 업데이트 (중복 충전을 방지하기 위해 플래그 업데이트)
        point.setCharged(true);

        // 포인트 정보 저장
        pointRepository.save(point);

        // 충전 후 응답 반환
        PointResponse pointResponse = PointResponse.builder()
                .pointBalance(point.getPointBalance())
                .build();

        return ResponseMessage.success("포인트가 정상적으로 충전됐습니다.", pointResponse);
    }
}
