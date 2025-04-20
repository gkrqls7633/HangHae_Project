package kr.hhplus.be.server.src.domain.user;

import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
