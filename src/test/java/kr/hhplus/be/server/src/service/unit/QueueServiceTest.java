package kr.hhplus.be.server.src.service.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.QueueRepository;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueResponse;
import kr.hhplus.be.server.src.service.QueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("해당 유저의 기존 활성화 토큰 존재하는 경우 토큰 갱신한다.")
    @Test
    void issueTokenWithExistsTokenTest() {

        // given
        Long mockUserId = 1L;
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setUserId(mockUserId);

        User mockUser = User.builder()
                .userId(mockUserId)
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .build();

        Queue queue = new Queue();
        queue.newToken();
        queue.setTokenStatus(TokenStatus.ACTIVE);

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
        when(queueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.of(queue));
        when(queueRepository.save(any(Queue.class))).thenReturn(queue);

        //when
        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);

        // Then
        assertEquals("대기열 토큰을 발급 완료했습니다.", response.getMessage());
        assertEquals(response.getData().getTokenStatus(), TokenStatus.ACTIVE);
        verify(queueRepository).save(queue);
    }

    @DisplayName("기존 토큰이 없는 경우 신규로 발급한다.")
    @Test
    void issueNewtTokenTest() {
        // given
        Long mockUserId = 2L;
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setUserId(mockUserId);

        User mockUser = User.builder()
                .userId(mockUserId)
                .userName("김테스트2")
                .phoneNumber("010-1234-1234")
                .email("test3@naver.com")
                .address("서울특별시 강서구 염창동")
                .build();

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
        when(queueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.empty());
        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> {
            Queue q = invocation.getArgument(0);
            q.setTokenStatus(TokenStatus.READY);
            q.setTokenValue("123def567");
            q.setIssuedAt(LocalDateTime.now());
            q.setExpiredAt(LocalDateTime.now().plusMinutes(5));
            return q;
        });

        // When
        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);

        // Then
        assertEquals("대기열 토큰을 발급 완료했습니다.", response.getMessage());
        assertEquals(TokenStatus.READY, response.getData().getTokenStatus());
        assertNotNull(response.getData().getTokenValue());
        verify(queueRepository).save(any(Queue.class));
    }

    @DisplayName("요청 유저가 없는 경우 에러 발생한다.")
    @Test
    void issuTokenWithNoUserTest() {
        // given
        Long mockUserId = 1L;
        QueueRequest queueRequest = new QueueRequest();
        queueRequest.setUserId(mockUserId);

        when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> queueService.issueQueueToken(queueRequest));
    }
}
