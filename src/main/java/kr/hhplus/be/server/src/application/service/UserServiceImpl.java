package kr.hhplus.be.server.src.application.service;

import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.domain.user.UserService;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
