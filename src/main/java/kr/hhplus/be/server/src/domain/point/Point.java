package kr.hhplus.be.server.src.domain.point;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Schema(description = "포인트 도메인")
public class Point {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Schema(description = "포인트 잔액", example = "10000")
    @JsonProperty("pointBalance")
    private Long pointBalance;


    public boolean isEnough(Long concertPrice) {

        //콘서트 가격보다 포인트가 충분한지 체크
        if (pointBalance == null || pointBalance < concertPrice) {
            return false;
        }

        return true;
    }

    // 콘서트 예약 후 잔여 포인트 차감 처리
    public Long usePoint(Long concertPrice) {

        if (concertPrice < 0) {
            throw new IllegalArgumentException("콘서트 가격은 0원 이상입니다.");
        }

        //로직 상 이미 isEnough는 체크된 후에 usePoint를 호출하므로 굳이 필요 없을듯.
        if (!this.isEnough(concertPrice)) {
            throw new IllegalStateException("포인트 잔액이 부족합니다.");
        }

        pointBalance -= concertPrice;
        return pointBalance;
    }


    //포인트 충전
    public void chargePoint(PointChargeRequest pointChargeRequest) {

        if (pointChargeRequest.getChargePoint() == null || pointChargeRequest.getChargePoint() <= 0) {
            throw new IllegalArgumentException("충전 금액은 0 이상이어야 합니다.");
        }

        this.pointBalance += pointChargeRequest.getChargePoint();

    }

}
