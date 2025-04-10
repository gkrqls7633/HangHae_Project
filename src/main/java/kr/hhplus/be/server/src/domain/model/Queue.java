package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Schema(description = "대기열 도메인")
@Table(name = "queue", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "booking_id"})
})
public class Queue {

    private static final long TOKEN_EXPIRE_MINUTES = 5;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "대기열 ID", example = "1", required = true)
    private Long queueId;

    @Schema(description = "토큰 값", example = "abc123token", required = true)
    private String tokenValue;

    @Schema(description = "토큰 발급 시간", example = "2025-04-10T15:30:00", required = true)
    private LocalDateTime issuedAt;

    @Schema(description = "토큰 만료 시간", example = "2025-04-11T15:30:00", required = true)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    //Booking과 1:1 관계
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* tokenValue 신규 발급
    - 토큰 만료 시간 : 5분
    */
    public void newToken() {
        this.tokenValue = UUID.randomUUID().toString();
        this.tokenStatus = TokenStatus.ACTIVE;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRE_MINUTES);
    }

    // 유효한 토큰이 존재하는지 확인
    public void validateActiveToken() {
        if (this.tokenStatus == TokenStatus.ACTIVE) {
            throw new IllegalStateException("이미 유효한 토큰이 존재합니다.");
        }
    }


}
