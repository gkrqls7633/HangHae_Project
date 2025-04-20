package kr.hhplus.be.server.src.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long userId);

    User save(User user);
}
