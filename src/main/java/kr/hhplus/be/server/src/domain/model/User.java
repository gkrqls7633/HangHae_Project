package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Schema(description = "유저 도메인")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @Schema(description = "유저Id", example = "1")
    private Long userId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Point point;

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

    @OneToMany(mappedBy = "user")
    private List<Queue> queues;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;


    public User(Long userId, String userName, String password, String phoneNumber, String email, String address) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }




}
