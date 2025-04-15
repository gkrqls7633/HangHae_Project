package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Point;

import java.util.List;

public interface PointRepositoryCustom {

    List<Point> findByPointBalanceGreaterThanEqual(Long amount);

}
