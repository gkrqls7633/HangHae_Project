package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserPoint(Long userId) {

        return userRepository.getUserPoint(userId);
    }
}
