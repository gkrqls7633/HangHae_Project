package kr.hhplus.be.server.src.domain.point.integration;

import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PointTransactionHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long setupTestData() {
        User user = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);

        Point point = Point.of(savedUser.getUserId(), savedUser, 200000L);
        pointRepository.save(point);

//        pointRepository.flush();
        return savedUser.getUserId();
    }
}
