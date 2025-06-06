package kr.hhplus.be.server.src.domain.queue.unit;

import kr.hhplus.be.server.src.application.service.queue.QueueServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.scehdular.QueueSchedular;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueScehdularTest {

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private QueueServiceImpl queueService;

    @InjectMocks
    private QueueSchedular queueScheduler;

    @DisplayName("토큰 만료 스케줄이 정상 작동한다.")
    @Test
    public void expireTokensTest() {
        // given
        Queue queue = Queue.newToken(123L);

        // 1. User 객체 설정
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);  // 예시로 User의 userId 설정

        // 2. tokenStatus 설정 (예시로 ACTIVE 상태로 설정)
        queue.setTokenStatus(TokenStatus.ACTIVE);

        when(queueService.findExpiredQueues(any(), any()))
                .thenReturn(List.of(queue));

        when(queueService.expireQueueToken(any(QueueExpireRequest.class)))
                .thenReturn(new ResponseMessage<>(200, "대기열 토큰이 만료됐습니다.", null));

        // when
        queueScheduler.expireTokens();

        // then
        verify(queueService, times(1)).expireQueueToken(any(QueueExpireRequest.class));
    }

    @DisplayName("토큰 활성화 스케줄이 정상 작동한다.")
    @Test
    public void readyToActiveTokensTest() {
        // given
        Queue queue = Queue.newToken(123L);

        // 1. User 객체 설정
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);  // 예시로 User의 userId 설정

        // 2. ready 상태 & 만료시간 지나지 않은 토큰 조회
        when(queueService.findReadToActivateTokens(any(), any()))
                .thenReturn(List.of(queue));

        // when
        queueScheduler.readyToActivateTokens();

        // then
        verify(queueService, times(1)).findReadToActivateTokens(any(TokenStatus.class), any(LocalDateTime.class));
        verify(queueService, times(1)).save(any(Queue.class));

    }

}