package kr.hhplus.be.server.src.domain.point;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;

public interface PointService {

    ResponseMessage<PointResponse> getPoint(Long userId);

    ResponseMessage<PointResponse> chargePoint(PointChargeRequest pointChargeRequest);

}
