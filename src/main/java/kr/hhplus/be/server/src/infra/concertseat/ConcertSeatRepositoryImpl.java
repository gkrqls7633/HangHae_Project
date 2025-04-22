package kr.hhplus.be.server.src.infra.concertseat;

import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeatRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertSeatRepositoryImpl implements ConcertSeatRepository {

    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    @Override
    public List<Seat> findAllSeatsByConcertId(Long concertId) {
        return concertSeatJpaRepository.findAllSeatsByConcertId(concertId);
    }

    @Override
    public ConcertSeat save(ConcertSeat concertSeat) {
        return concertSeatJpaRepository.save(concertSeat);
    }

    @Override
    public void deleteAll() {
        concertSeatJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        concertSeatJpaRepository.deleteAllInBatch();
    }
}
