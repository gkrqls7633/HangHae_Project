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
        User user = new User();
        user.setUserName("김항해");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@naver.com");
        user.setAddress("서울특별시 강서구 염창동");
        User savedUser = userRepository.save(user);

        Point point = new Point();
        point.setUser(savedUser);
        point.setPointBalance(200000L);
        pointRepository.save(point);

//        pointRepository.flush();
        return savedUser.getUserId();
    }
}
