package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    /* 유저의 활성화 상태의 토큰이 존재하는지 조회 */
    Optional<Queue> findByBooking_UserIdAndTokenStatus(Long userId, TokenStatus tokenStatus);
}
