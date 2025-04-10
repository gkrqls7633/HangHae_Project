package kr.hhplus.be.server.src.infrastructure.repository;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Override
    public UserResponse getUserPoint(String userId) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userId);
        userResponse.setPoint(1000L);

        return userResponse;
    }
}
