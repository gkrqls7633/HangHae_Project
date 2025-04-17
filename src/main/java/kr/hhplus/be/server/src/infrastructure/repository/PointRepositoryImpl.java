package kr.hhplus.be.server.src.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Point> findByPointBalanceGreaterThanEqual(Long amount) {
        String jpql = "SELECT p FROM Point p WHERE p.pointBalance >= :amount";

        TypedQuery<Point> query = em.createQuery(jpql, Point.class);
        query.setParameter("amount", amount);

        return query.getResultList();
    }
}
