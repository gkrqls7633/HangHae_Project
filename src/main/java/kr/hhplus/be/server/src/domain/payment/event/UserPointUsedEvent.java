package kr.hhplus.be.server.src.domain.payment.event;

import kr.hhplus.be.server.src.domain.point.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPointUsedEvent {

    private Point point;

    private Long price;

}
