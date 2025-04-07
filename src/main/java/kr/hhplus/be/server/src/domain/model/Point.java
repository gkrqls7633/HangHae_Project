package kr.hhplus.be.server.src.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Schema(description = "포인트 정보")
public class Point {


    @Id
    @Schema(description = "userId", example = "1")
    @JsonProperty("userId")
    private String userId;

    @Schema(description = "포인트 잔액", example = "10000")
    @JsonProperty("pointBalance")
    private Long pointBalance;

    public Point(String userId) {
        this.userId = userId;
        this.pointBalance = getPointBalanceFromUser(userId); // 포인트 잔액 설정
    }

    public Point(String userId, Long pointBalance) {
        this.userId = userId;
        this.pointBalance = pointBalance;
    }

    private Long getPointBalanceFromUser(String userId) {
        User user = new User(userId);
        return user.getPointBalance();
    }

    public Long getPointBalance(String userId) {

        User user = new User(userId);
        return user.getPointBalance();

    }
    public void setPointBalance(Long pointBalance) {
        this.pointBalance = pointBalance;
    }

}
