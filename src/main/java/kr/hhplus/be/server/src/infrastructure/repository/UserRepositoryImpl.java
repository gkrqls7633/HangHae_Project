package kr.hhplus.be.server.src.infrastructure.repository;

import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserResponse getUserPoint(Long userId) {
        String jpql = "SELECT p FROM Point p WHERE p.userId = :userId";
        TypedQuery<Point> query = em.createQuery(jpql, Point.class);
        query.setParameter("userId", userId);

        try {
            Point point = query.getSingleResult();

            // Point → UserResponse로 변환
            return UserResponse.builder()
                    .userId(point.getUser().getUserId())
                    .point(point.getPointBalance())
                    .build();

        } catch (NoResultException e) {
            throw new EntityNotFoundException("해당 유저의 포인트 정보가 없습니다.");
        }    }
}
