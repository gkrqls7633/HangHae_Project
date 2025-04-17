
package kr.hhplus.be.server.src.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class QueueRepositoryImpl implements QueueRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<Queue> findByExpiredAtBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus) {
        String jpql = "SELECT q FROM Queue q WHERE q.expiredAt < :now AND q.tokenStatus = :status";
        TypedQuery<Queue> query = em.createQuery(jpql, Queue.class);
        query.setParameter("now", now);
        query.setParameter("status", tokenStatus);
        return query.getResultList();
    }
}
