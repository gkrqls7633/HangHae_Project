package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.point.PointService;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    private static final int MAX_RETRY = 3;

    /**
     * @description 유저의 포인트 잔액을 조회한다.
     * @param userId
     * @return 포인트 잔액
     */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ResponseMessage<PointResponse> getPoint(Long userId) {

        Point point = pointRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보가 없습니다."));

        Long userPointBalance = point.getPointBalance();

        PointResponse pointResponse = PointResponse.builder()
                .pointBalance(userPointBalance)
                .build();

        return ResponseMessage.success("포인트 잔액이 정상적으로 조회됐습니다.", pointResponse);
    }

    @Transactional
    public ResponseMessage<PointResponse> chargePointWithRetry(PointChargeRequest request) {
        int retryCount = 0;

        while (retryCount < MAX_RETRY) {
            try {
                return chargePoint(request);
            } catch (OptimisticLockException e) {
                retryCount++;
                if (retryCount >= MAX_RETRY) {
                    throw new RuntimeException("포인트 충전에 실패했습니다. 다시 시도해주세요.", e);
                }
                try {
                    Thread.sleep(100); // 잠시 대기 후 재시도
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("알 수 없는 이유로 포인트 충전에 실패했습니다.");
    }

    /**
     * @description 유저의 포인트를 충전한다.
     * @param pointChargeRequest
     * @return
     */
    @Override
    @Transactional
    public ResponseMessage<PointResponse> chargePoint(PointChargeRequest pointChargeRequest) {

        //현재 잔액 조회
        Point point = pointRepository.findById(pointChargeRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("포인트 정보가 없습니다."));

        //충전 호출
        point.chargePoint(pointChargeRequest);

        //충전 저장
        pointRepository.save(point);

        PointResponse pointResponse = PointResponse.builder()
                .pointBalance(point.getPointBalance())
                .build();

        return ResponseMessage.success("포인트가 정상적으로 충전됐습니다.", pointResponse);
    }
}
