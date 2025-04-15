package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueRepositoryCustom {

    List<Queue> findExpiredQueues(LocalDateTime now, TokenStatus tokenStatus);


}
