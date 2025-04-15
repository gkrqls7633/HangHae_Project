
package kr.hhplus.be.server.src.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.hhplus.be.server.src.domain.model.Seat;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SeatRepositoryImpl implements SeatRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<Seat> findByConcertSeat_Concert_ConcertIdAndSeatNum(Long concertId, Long seatNum) {
        String jpql = "SELECT s FROM Seat s "
                + "JOIN s.concertSeat c "
                + "JOIN c.concert co "
                + "WHERE co.concertId = :concertId AND s.seatNum = :seatNum";

        TypedQuery<Seat> query = em.createQuery(jpql, Seat.class);
        query.setParameter("concertId", concertId);
        query.setParameter("seatNum", seatNum);

        try {
            Seat seat = query.getSingleResult();
            return Optional.of(seat);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
