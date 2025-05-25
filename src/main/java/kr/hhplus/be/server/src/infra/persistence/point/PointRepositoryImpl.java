package kr.hhplus.be.server.src.infra.persistence.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    @PersistenceContext
    private EntityManager em;

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findById(Long userId) {
        return pointJpaRepository.findById(userId);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public List<Point> findByPointBalanceGreaterThanEqual(Long amount) {
        String jpql = "SELECT p FROM Point p WHERE p.pointBalance >= :amount";

        TypedQuery<Point> query = em.createQuery(jpql, Point.class);
        query.setParameter("amount", amount);

        return query.getResultList();
    }

    @Override
    public void deleteAllInBatch() {
        pointJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(Long userId) {
        String jpql = "SELECT p FROM Point p WHERE p.userId = :userId";

        try {
            Point point = em.createQuery(jpql, Point.class)
                    .setParameter("userId", userId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // 비관적 락 적용
                    .getSingleResult();

            return Optional.of(point);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void flush() {
        pointJpaRepository.flush();
    }
}
