package kr.hhplus.be.server.src.infra.persistence.user;

import kr.hhplus.be.server.src.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

}
