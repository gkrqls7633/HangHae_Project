package kr.hhplus.be.server.src.infra.persistence.queue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    @PersistenceContext
    private EntityManager em;

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Optional<Queue> findByUserIdAndTokenStatus(Long userId, TokenStatus tokenStatus) {
        return queueJpaRepository.findByUserIdAndTokenStatus(userId, tokenStatus);
    }

    @Override
    public List<Queue> findAllByTokenStatus(TokenStatus tokenStatus) {
        return queueJpaRepository.findAllByTokenStatus(tokenStatus);
    }

    @Override
    public List<Queue> findByTokenStatusAndExpiredAtAfter(TokenStatus tokenStatus, LocalDateTime currentTime) {
        return queueJpaRepository.findByTokenStatusAndExpiredAtAfter(tokenStatus, currentTime);
    }

    @Override
    public Queue save(Queue activeQueue) {
        return queueJpaRepository.save(activeQueue);
    }

    @Override
    public Optional<Queue> findById(Long queueId) {
        return queueJpaRepository.findById(queueId);
    }

    @Override
    public List<Queue> findByExpiredAtBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus) {
        String jpql = "SELECT q FROM Queue q WHERE q.expiredAt < :now AND q.tokenStatus = :status";
        TypedQuery<Queue> query = em.createQuery(jpql, Queue.class);
        query.setParameter("now", now);
        query.setParameter("status", tokenStatus);
        return query.getResultList();
    }

    @Override
    public Optional<Queue> findByTokenValueAndTokenStatus(String tokenValue, TokenStatus tokenStatus) {
        return queueJpaRepository.findByTokenValueAndTokenStatus(tokenValue, tokenStatus);
    }

    @Override
    public Queue findByTokenStatus(TokenStatus tokenStatus) {
        return queueJpaRepository.findByTokenStatus(tokenStatus);
    }
}
