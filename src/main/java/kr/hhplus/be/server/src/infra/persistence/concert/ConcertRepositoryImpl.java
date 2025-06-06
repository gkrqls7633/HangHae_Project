package kr.hhplus.be.server.src.infra.persistence.concert;

import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public Optional<Concert> findById(Long concertId) {
        return concertJpaRepository.findById(concertId);
    }

    @Override
    public List<Concert> findAll() {
        return concertJpaRepository.findAll();
    }

    @Override
    public Concert save(Concert concert) {
        return concertJpaRepository.save(concert);
    }

    @Override
    public void deleteAll() {
        concertJpaRepository.deleteAll();
    }

    @Override
    public void deleteAllInBatch() {
        concertJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<Concert> findConcertsStartingBefore(LocalDateTime now) {
        return concertJpaRepository.findByConcertStartDateBefore(now);
    }
}
