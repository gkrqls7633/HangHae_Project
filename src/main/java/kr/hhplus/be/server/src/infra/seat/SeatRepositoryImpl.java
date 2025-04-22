package kr.hhplus.be.server.src.infra.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    @PersistenceContext
    private EntityManager em;

    private final SeatJpaRepository seatJpaRepository;

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
        }    }

    @Override
    public Seat save(Seat seat) {
        return  seatJpaRepository.save(seat);
    }

    @Override
    public Optional<Seat> findById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public void saveAll(List<Seat> seatList) {
        seatJpaRepository.saveAll(seatList);
    }

    @Override
    public void deleteAll() {
        seatJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        seatJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Seat> findByConcertIdAndSeatNumWithLock(Long concertId, Long seatNum) {
        try {
            Seat seat = em.createQuery(
                            "SELECT s FROM Seat s WHERE s.concertSeat.concert.concertId = :concertId AND s.seatNum = :seatNum", Seat.class)
                    .setParameter("concertId", concertId)
                    .setParameter("seatNum", seatNum)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
            return Optional.of(seat);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
