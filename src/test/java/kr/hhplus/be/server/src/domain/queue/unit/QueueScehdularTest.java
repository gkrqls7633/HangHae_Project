package kr.hhplus.be.server.src.domain.queue.unit;

import kr.hhplus.be.server.src.application.service.QueueServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.interfaces.queue.QueueExpireRequest;
import kr.hhplus.be.server.src.domain.queue.QueueService;
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
        Queue queue = new Queue();

        // 1. User 객체 설정
        User mockUser = new User();
        mockUser.setUserId(123L);  // 예시로 User의 userId 설정

        // 2. tokenValue 설정
        queue.setTokenValue("abc123token");

        // 3. issuedAt 설정 (현재 시간 설정)
        LocalDateTime now = LocalDateTime.now(); // 현재 시간을 고정
        queue.setIssuedAt(now);

        // 4. expiredAt 설정 (5분 후 시간 설정)
        queue.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        // 5. tokenStatus 설정 (예시로 ACTIVE 상태로 설정)
        queue.setTokenStatus(TokenStatus.ACTIVE);

        when(queueRepository.findByExpiredAtBeforeAndTokenStatus(any(), any()))
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
        Queue queue = new Queue();

        // 1. User 객체 설정
        User mockUser = new User();
        mockUser.setUserId(123L);  // 예시로 User의 userId 설정

        // 2. tokenValue 설정
        queue.setTokenValue("abc123token");

        // 3. issuedAt 설정 (현재 시간 설정)
        LocalDateTime now = LocalDateTime.now(); // 현재 시간을 고정
        queue.setIssuedAt(now);

        // 4. expiredAt 설정 (5분 후 시간 설정)
        queue.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        // 5. tokenStatus 설정 (예시로 ACTIVE 상태로 설정)
        queue.setTokenStatus(TokenStatus.READY);

        when(queueRepository.findByTokenStatusAndExpiredAtAfter(any(), any()))
                .thenReturn(List.of(queue));

        // when
        queueScheduler.readyToActivateTokens();

        // then
        verify(queueRepository, times(1)).findByTokenStatusAndExpiredAtAfter(any(TokenStatus.class), any(LocalDateTime.class));
        verify(queueRepository, times(1)).save(any(Queue.class));

    }

}