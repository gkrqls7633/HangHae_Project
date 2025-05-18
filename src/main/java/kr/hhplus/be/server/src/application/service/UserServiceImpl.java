package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.domain.user.UserService;
import kr.hhplus.be.server.src.interfaces.user.UserQueueRankResponse;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RedisQueueRepository redisQueueRepository;

    @Override
    public UserResponse getUserPoint(Long userId) {

        UserResponse userResponse = new UserResponse();
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            userResponse.setUserId(userId);
            userResponse.setPoint(user.get().getPoint().getPointBalance());
        }

        return userResponse;
    }

    @Override
    public UserQueueRankResponse getUserQueueRank(Long userId) {

        String tokenValue = redisQueueRepository.getUserTokenValue(userId);

        Set<String> topTokens = redisQueueRepository.getReadyTokens(tokenValue, LocalDateTime.now());

        if (topTokens == null || topTokens.isEmpty()) {
            throw new EntityNotFoundException("현재 대기열에 유효한 토큰이 없습니다.");
        }

        //순위 계산
        String tokenKey = "token:" + tokenValue;
        List<String> tokenList = new ArrayList<>(topTokens);

        int index = tokenList.indexOf(tokenKey);
        if (index == -1) {
            throw new EntityNotFoundException("해당 유저의 토큰은 대기열 내에 존재하지 않습니다.");
        }

        return UserQueueRankResponse.builder()
                .userId(userId)
                .rank(index + 1)
                .build();
    }
}
