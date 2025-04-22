package kr.hhplus.be.server.src.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.point.Point;
import lombok.*;

import java.util.List;

@Entity
@Schema(description = "유저 도메인")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@Table(name = "`user`")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "유저Id", example = "1")
    private Long userId;

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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Point point;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<Booking> bookings;

    public static User of(String userName, String password, String phoneNumber, String email, String address) {
        return User.builder()
                .userName(userName)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .address(address)
                .build();
    }

}
