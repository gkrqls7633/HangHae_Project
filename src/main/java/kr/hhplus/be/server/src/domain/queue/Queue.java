package kr.hhplus.be.server.src.domain.queue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Schema(description = "대기열 도메인")
@Table(name = "queue",
        indexes = {
                @Index(name = "idx_issued_at", columnList = "issued_at"),
                @Index(name = "idx_expired_at", columnList = "expired_at"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_token_status_expired_at", columnList = "token_status, expired_at")
        },
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"}) //유저는 항상 하나의 유효한 토큰만 갖는다
})

public class Queue extends BaseTimeEntity {


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

    @Schema(description = "발급 대상 userId", example = "123L", required = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_status", length = 10)
    private TokenStatus tokenStatus;

    /* tokenValue 신규 발급
    - 토큰 만료 시간 : 5분
    - 대기 상태 토큰 발급
    */
    public static Queue newToken() {
        return Queue.builder()
                .tokenValue(UUID.randomUUID().toString())
                .tokenStatus(TokenStatus.READY)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRE_MINUTES))
                .build();
    }

    // 유효한 토큰(대기중)이 존재하는지 확인
    public boolean validateActiveToken() {
        return this.tokenStatus == TokenStatus.READY || this.tokenStatus == TokenStatus.ACTIVE;
    }


    //유저 토큰 갱신 (다시 현재 시간부터 5분 연장)
    public void refreshToken() {
        if (this.tokenStatus == TokenStatus.READY) {
            this.issuedAt = LocalDateTime.now();
            this.expiredAt = this.issuedAt.plusMinutes(TOKEN_EXPIRE_MINUTES);
        }
    }

    //토큰 만료 여부 체크
    public boolean isExpired() {
        return (tokenStatus == TokenStatus.ACTIVE || tokenStatus == TokenStatus.READY) && expiredAt.isBefore(LocalDateTime.now());
    }

    /**
     * 비즈니스 정책
     */
    //✔️ 유저는 항상 하나의 유효한 토큰만 갖는다
    //✔️ 신규 발급 시에도 기존 토큰 여부를 꼭 체크하고, 갱신(update) 으로 처리

    private static final long TOKEN_EXPIRE_MINUTES = 5;

}
