package kr.hhplus.be.server.src.domain.concert;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    Optional<Concert> findById(Long concertId);

    List<Concert> findAll();

    Concert save(Concert concert);

    void deleteAll();

    void deleteAllInBatch();

    List<Concert> findConcertsStartingBefore(LocalDateTime now);
}
