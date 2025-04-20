package kr.hhplus.be.server.src.infra.user;

import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

//    @Override
//    public UserResponse getUserPoint(Long userId) {
//        String jpql = "SELECT p FROM Point p WHERE p.userId = :userId";
//        TypedQuery<Point> query = em.createQuery(jpql, Point.class);
//        query.setParameter("userId", userId);
//
//        try {
//            Point point = query.getSingleResult();
//
//            // Point → UserResponse로 변환
//            return UserResponse.builder()
//                    .userId(point.getUser().getUserId())
//                    .point(point.getPointBalance())
//                    .build();
//
//        } catch (NoResultException e) {
//            throw new EntityNotFoundException("해당 유저의 포인트 정보가 없습니다.");
//        }
//    }
}
