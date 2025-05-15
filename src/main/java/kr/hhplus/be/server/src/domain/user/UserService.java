package kr.hhplus.be.server.src.domain.user;

import kr.hhplus.be.server.src.interfaces.user.UserQueueRankResponse;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;

public interface UserService {

    UserResponse getUserPoint(Long userId);

    UserQueueRankResponse getUserQueueRank(Long userId);
}
