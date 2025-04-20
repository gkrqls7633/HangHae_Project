package kr.hhplus.be.server.src.infra.point;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
}
