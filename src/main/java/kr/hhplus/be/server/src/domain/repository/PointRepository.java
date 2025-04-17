package kr.hhplus.be.server.src.domain.repository;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.infrastructure.repository.PointRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {


}
