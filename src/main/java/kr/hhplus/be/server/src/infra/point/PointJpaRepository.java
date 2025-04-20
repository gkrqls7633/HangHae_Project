package kr.hhplus.be.server.src.infra.point;

import kr.hhplus.be.server.src.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
}
