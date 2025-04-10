package kr.hhplus.be.server.src.domain.repository;


import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.infrastructure.repository.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> , UserRepositoryCustom {

}
