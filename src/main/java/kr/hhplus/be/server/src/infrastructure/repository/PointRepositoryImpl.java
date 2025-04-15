package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepositoryCustom {


    @Override
    public List<Point> findByPointBalanceGreaterThanEqual(Long amount) {
        return null;
    }
}
