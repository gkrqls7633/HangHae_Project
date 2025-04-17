package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepositoryCustom {

    UserResponse getUserPoint(Long userId);
}
