package kr.hhplus.be.server.src.domain.point;

import java.util.List;
import java.util.Optional;

public interface PointRepository {
    Optional<Point> findById(Long userId);

    Point save(Point point);

    List<Point> findByPointBalanceGreaterThanEqual(Long amount);

    void deleteAllInBatch();
}
