package kr.hhplus.be.server.src.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Schema(description = "포인트 정보")
public class Point {

    @Id
    @Schema(description = "userId", example = "1")
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "포인트 잔액", example = "10000")
    @JsonProperty("pointBalance")
    private Long pointBalance;

    public Long getPointBalance(String userId) {

        User user = new User(userId);
        return user.getPointBalance();

    }

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
            throw new IllegalArgumentException("Concert price must be positive.");
        }

        //로직 상 이미 isEnough는 체크된 후에 usePoint를 호출하므로 굳이 필요 없을듯.
        if (!this.isEnough(concertPrice)) {
            throw new IllegalStateException("Not enough point balance.");
        }

        pointBalance -= concertPrice;
        return pointBalance;
    }
}
