package kr.hhplus.be.server.src.service.testTransactionHelper;

import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.repository.*;
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueTransactionHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueRepository queueRepository;

    private QueueRequest queueRequest;

    public QueueRequest setupTestData() {

        User user = User.builder()
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();
        User savedUser = userRepository.save(user);

        Queue queue = new Queue();
        queue.newToken();
        queueRepository.save(queue);

        queueRequest = new QueueRequest();
        queueRequest.setUserId(savedUser.getUserId());

        return queueRequest;
    }

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

        return queueRequest;
    }
}
