package kr.hhplus.be.server.src.domain.point;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.api.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.api.point.dto.PointResponse;

public interface PointService {

    ResponseMessage<PointResponse> getPoint(Long userId);

    ResponseMessage<PointResponse> chargePoint(PointChargeRequest pointChargeRequest);

    ResponseMessage<PointResponse> chargePointWithLock(PointChargeRequest pointChagrgeRequest);

    ResponseMessage<PointResponse> chargePointWithPessimisticLock(PointChargeRequest pointChargeRequest);
}
