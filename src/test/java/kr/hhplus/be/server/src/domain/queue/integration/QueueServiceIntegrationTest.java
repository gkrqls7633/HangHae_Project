package kr.hhplus.be.server.src.domain.queue.integration;

import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueResponse;
import kr.hhplus.be.server.src.domain.queue.QueueService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class QueueServiceIntegrationTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueTransactionHelper queueTransactionHelper;

    private QueueRequest queueRequest;

    @Nested
    class WithSetup {


        @BeforeEach
        void setUp() {
            queueRequest = queueTransactionHelper.setupTestData();
        }

        @DisplayName("유저 정보 체크 후 토큰 갱신 처리한다.")
        @Test
        void issueQueueToken() {

            //given
            Long userId =  queueRequest.getUserId();

            //when
            ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
            LocalDateTime now = LocalDateTime.now();

            //then
            //갱신 후 다시 Ready 상태인지 확인
            assertEquals(response.getData().getTokenStatus(), TokenStatus.READY);

            //만료시간 갱신 확인
            Assert.assertTrue(response.getData().getExpiredAt().isAfter(now));

            //발급시간 갱신 확인
            Duration duration = Duration.between(response.getData().getIssuedAt(), response.getData().getExpiredAt());
            Assert.assertTrue("expiredAt should be 5 minutes after issuedAt, but difference is " + duration.toMinutes() + " minutes.",
                    duration.toMinutes() == 5);
        }
    }

    @DisplayName("유저 정보 체크 후 토큰 신규 발급 처리한다.")
    @Test
    void issueNewQueueToken() {

        QueueRequest queueRequest = queueTransactionHelper.setupTestDataWithNoQueue();

        //given
        Long userId = queueRequest.getUserId();

        //when
        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
        LocalDateTime now = LocalDateTime.now();

        //then
        //갱신 후 다시 Ready 상태인지 확인
        assertEquals(response.getData().getTokenStatus(), TokenStatus.READY);

        //만료시간 갱신 확인
        Assert.assertTrue(response.getData().getExpiredAt().isAfter(now));

        //발급시간 갱신 확인
        Duration duration = Duration.between(response.getData().getIssuedAt(), response.getData().getExpiredAt());
        Assert.assertTrue("expiredAt should be 5 minutes after issuedAt, but difference is " + duration.toMinutes() + " minutes.",
                duration.toMinutes() == 5);
    }
}
