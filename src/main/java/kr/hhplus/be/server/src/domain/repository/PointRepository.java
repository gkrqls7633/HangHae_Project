package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
