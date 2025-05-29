package kr.hhplus.be.server.src.domain.queue.integration;

import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QueueTransactionHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private RedisQueueRepository redisQueueRepository;

    private QueueRequest queueRequest;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public QueueRequest setupTestData() {

        User user = User.builder()
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();
        User savedUser = userRepository.save(user);

        Queue queue = Queue.newToken(savedUser.getUserId());
        queue.setTokenStatus(TokenStatus.ACTIVE);  //바로 활성화 위해
        redisQueueRepository.save(queue);

        queueRequest = new QueueRequest();
        queueRequest.setUserId(savedUser.getUserId());
        queueRequest.setConcertId(1L);

        return queueRequest;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public QueueRequest setupTestDataWithNoQueue() {

        User user = User.builder()
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();
        User savedUser = userRepository.save(user);

        queueRequest = new QueueRequest();
        queueRequest.setUserId(savedUser.getUserId());
        queueRequest.setConcertId(1L);

        return queueRequest;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public QueueRequest setupTestDataExistingExpiredQueue() {

        User user = User.builder()
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();
        User savedUser = userRepository.save(user);

        Queue queue = Queue.newToken(savedUser.getUserId());
        queue.setTokenStatus(TokenStatus.EXPIRED); //만료된 토큰 존재
        redisQueueRepository.save(queue);

        queueRequest = new QueueRequest();
        queueRequest.setUserId(savedUser.getUserId());
        queueRequest.setConcertId(1L);

        return queueRequest;
    }
}
