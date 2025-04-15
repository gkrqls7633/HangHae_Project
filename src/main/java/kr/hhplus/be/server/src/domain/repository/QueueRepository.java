package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.infrastructure.repository.QueueRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long>, QueueRepositoryCustom {

    /* 특정 유저의 활성화 상태의 토큰이 존재하는지 조회 */
    @Query("SELECT q FROM Queue q WHERE q.booking.user.userId = :userId AND q.tokenStatus = :tokenStatus")
    Optional<Queue> findByBookingUserIdAndTokenStatus(@Param("userId") Long userId, @Param("tokenStatus") TokenStatus tokenStatus);

    @Query("SELECT q FROM Queue q WHERE q.booking.bookingId = :bookingId AND q.user.userId = :userId AND q.tokenStatus = :tokenStatus")
    Queue findByBookingIdAndUserIdAndTokenStatus(
            @Param("bookingId") Long bookingId,
            @Param("userId") Long userId,
            @Param("tokenStatus") TokenStatus tokenStatus
    );
}
