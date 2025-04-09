package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Schema(description = "유저 정보")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @Schema(description = "유저Id", example = "1")
    private String userId;

    @Schema(description = "유저명", example = "김항해")
    private String userName;

    @Schema(description = "비밀번호", example = "12345")
    private String password;

    @Schema(description = "유저 휴대번호", example = "010-1234-5678")
    private String phoneNumber;

    @Schema(description = "email 주소", example = "test@naver.com")
    private String email;

    @Schema(description = "주소", example = "서울특별시 강서구 염창동")
    private String address;

    @Schema(description = "포인트 정보",
            example = "{ \"pointBalance\": 10000 }")
    private Long pointBalance;

    public User(String userId, String userName, String password, String phoneNumber, String email, String address) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public User(String userId) {
        this.userId = userId;
        this.pointBalance = 10000L;
    }



    public Point chargePoint(PointChargeRequest pointChargeRequest) {
        User user = new User(userId);
        user.setPointBalance(user.getPointBalance() + pointChargeRequest.getChargePoint());

        return new Point(pointChargeRequest.getUserId(), user.getPointBalance());
    }

}
