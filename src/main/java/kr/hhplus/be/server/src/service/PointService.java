package kr.hhplus.be.server.src.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    /**
     * @description 유저의 포인트 잔액을 조회한다.
     * @param userId
     * @return 포인트 잔액
     */
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

    /**
     * @description 유저의 포인트를 충전한다.
     * @param pointChargeRequest
     * @return
     */
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
