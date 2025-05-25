package kr.hhplus.be.server.src.domain.user;

import kr.hhplus.be.server.src.interfaces.api.user.UserQueueRankResponse;
import kr.hhplus.be.server.src.interfaces.api.user.UserResponse;

public interface UserService {

    UserResponse getUserPoint(Long userId);

    UserQueueRankResponse getUserQueueRank(Long userId);
}
