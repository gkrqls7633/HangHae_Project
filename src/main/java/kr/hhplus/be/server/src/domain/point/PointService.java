package kr.hhplus.be.server.src.domain.point;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;

public interface PointService {

    ResponseMessage<PointResponse> getPoint(Long userId);

    ResponseMessage<PointResponse> chargePoint(PointChargeRequest pointChargeRequest);

//    ResponseMessage<PointResponse> chargePointWithRetry(PointChargeRequest pointChagrgeRequest);

    ResponseMessage<PointResponse> chargePointWithLock(PointChargeRequest pointChagrgeRequest);
}
