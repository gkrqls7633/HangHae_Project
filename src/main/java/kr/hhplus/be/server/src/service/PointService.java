package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public ResponseMessage<PointResponse> getPoint(String userId) {

//        Point point = new Point();

        PointResponse pointResponse = new PointResponse();
        pointResponse.setPointBalance(100000L);

        return ResponseMessage.success("포인트 잔액이 정상적으로 조회됐습니다.", pointResponse);

    }
}
