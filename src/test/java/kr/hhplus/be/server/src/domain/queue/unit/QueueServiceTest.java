package kr.hhplus.be.server.src.domain.queue.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.application.service.queue.QueueServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueRequest;
import kr.hhplus.be.server.src.interfaces.api.queue.dto.QueueResponse;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @InjectMocks
    private QueueServiceImpl queueService;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisQueueRepository redisQueueRepository;

//    @DisplayName("해당 유저의 기존 활성화 토큰 존재하는 경우 토큰 갱신한다. - RDB")
//    @Test
//    void issueTokenWithExistsTokenTest() {
//
//        // given
//        Long mockUserId = 1L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        User mockUser = User.builder()
//                .userId(mockUserId)
//                .userName("김테스트")
//                .phoneNumber("010-1234-5678")
//                .email("test2@naver.com")
//                .address("서울특별시 강서구 등촌동")
//                .build();
//
//        Queue queue = Queue.newToken(mockUser.getUserId());
//        queue.setTokenStatus(TokenStatus.ACTIVE);
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
//        when(queueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.of(queue));
//        when(queueRepository.save(any(Queue.class))).thenReturn(queue);
//
//        //when
//        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
//
//        // Then
//        assertEquals("대기열 토큰을 갱신 완료했습니다.", response.getMessage());
//        assertEquals(response.getData().getTokenStatus(), TokenStatus.ACTIVE);
//        verify(queueRepository).save(queue);
//    }

//    @DisplayName("해당 유저의 기존 활성화 토큰 존재하는 경우 토큰 갱신한다. - REDIS")
//    @Test
//    void issueTokenWithExistsTokenTest() {
//
//        // given
//        Long mockUserId = 1L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        User mockUser = User.builder()
//                .userId(mockUserId)
//                .userName("김테스트")
//                .phoneNumber("010-1234-5678")
//                .email("test2@naver.com")
//                .address("서울특별시 강서구 등촌동")
//                .build();
//
//        Queue queue = Queue.newToken(mockUser.getUserId());
//        queue.setTokenStatus(TokenStatus.ACTIVE);
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
//        when(redisQueueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.of(queue));
//        when(redisQueueRepository.save(any(Queue.class))).thenReturn(queue);
//
//        //when
//        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
//
//        // Then
//        assertEquals("대기열 토큰을 갱신 완료했습니다.", response.getMessage());
//        assertEquals(response.getData().getTokenStatus(), TokenStatus.ACTIVE);
//        verify(redisQueueRepository).save(queue);
//    }

//    @DisplayName("기존 토큰이 없는 경우 신규로 발급한다. - RDB")
//    @Test
//    void issueNewtTokenTest() {
//        // given
//        Long mockUserId = 2L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        User mockUser = User.builder()
//                .userId(mockUserId)
//                .userName("김테스트2")
//                .phoneNumber("010-1234-1234")
//                .email("test3@naver.com")
//                .address("서울특별시 강서구 염창동")
//                .build();
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
//        when(queueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.empty());
//        when(queueRepository.save(any(Queue.class))).thenAnswer(invocation -> {
//            Queue q = invocation.getArgument(0);
//            q.setTokenStatus(TokenStatus.READY);
//            q.setTokenValue("123def567");
//            q.setIssuedAt(LocalDateTime.now());
//            q.setExpiredAt(LocalDateTime.now().plusMinutes(5));
//            return q;
//        });
//
//        // When
//        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
//
//        // Then
//        assertEquals("대기열 토큰을 발급 완료했습니다.", response.getMessage());
//        assertEquals(TokenStatus.READY, response.getData().getTokenStatus());
//        assertNotNull(response.getData().getTokenValue());
//        verify(queueRepository).save(any(Queue.class));
//    }

//    @DisplayName("기존 토큰이 없는 경우 신규로 발급한다. - REDIS")
//    @Test
//    void issueNewtTokenTest() {
//        // given
//        Long mockUserId = 2L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        User mockUser = User.builder()
//                .userId(mockUserId)
//                .userName("김테스트2")
//                .phoneNumber("010-1234-1234")
//                .email("test3@naver.com")
//                .address("서울특별시 강서구 염창동")
//                .build();
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
//        when(redisQueueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.empty());
//        when(redisQueueRepository.save(any(Queue.class))).thenAnswer(invocation -> {
//            Queue q = invocation.getArgument(0);
//            q.setTokenStatus(TokenStatus.READY);
//            q.setTokenValue("123def567");
//            q.setIssuedAt(LocalDateTime.now());
//            q.setExpiredAt(LocalDateTime.now().plusMinutes(5));
//            return q;
//        });
//
//        // When
//        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
//
//        // Then
//        assertEquals("대기열 토큰을 발급 완료했습니다.", response.getMessage());
//        assertEquals(TokenStatus.READY, response.getData().getTokenStatus());
//        assertNotNull(response.getData().getTokenValue());
//        verify(redisQueueRepository).save(any(Queue.class));
//    }

//    @DisplayName("만료 토큰 갱신 요청 시 기존 토큰 제거 및 신규 토큰 발급한다. - REDIS")
//    @Test
//    void expiredtTokenIssueTest() {
//
//        // given
//        Long mockUserId = 2L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        User mockUser = User.builder()
//                .userId(mockUserId)
//                .userName("김테스트2")
//                .phoneNumber("010-1234-1234")
//                .email("test3@naver.com")
//                .address("서울특별시 강서구 염창동")
//                .build();
//
//        Queue expiredQueue = Queue.builder()
//                .userId(mockUserId)
//                .tokenValue("expired-token-123")
//                .tokenStatus(TokenStatus.EXPIRED)
//                .issuedAt(LocalDateTime.now().minusMinutes(10))
//                .expiredAt(LocalDateTime.now().minusMinutes(1))
//                .build();
//
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));
//        when(redisQueueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.ACTIVE)).thenReturn(Optional.empty());
//        when(redisQueueRepository.findByUserIdAndTokenStatus(mockUserId, TokenStatus.EXPIRED)).thenReturn(Optional.of(expiredQueue));
//
//        when(redisQueueRepository.save(any(Queue.class))).thenAnswer(invocation -> {
//            Queue q = invocation.getArgument(0);
//            q.setTokenStatus(TokenStatus.READY);
//            q.setTokenValue("new-token-456");
//            q.setIssuedAt(LocalDateTime.now());
//            q.setExpiredAt(LocalDateTime.now().plusMinutes(5));
//            return q;
//        });
//
//
//        // When
//        ResponseMessage<QueueResponse> response = queueService.issueQueueToken(queueRequest);
//
//        // then
//        assertEquals("대기열 토큰을 갱신 완료했습니다.", response.getMessage());
//        assertEquals(TokenStatus.READY, response.getData().getTokenStatus());
//        assertEquals("new-token-456", response.getData().getTokenValue());
//
//        verify(redisQueueRepository).removeExpiredQueue(expiredQueue);
//        verify(redisQueueRepository).save(any(Queue.class));
//
//    }

//    @DisplayName("요청 유저가 없는 경우 에러 발생한다.")
//    @Test
//    void issuTokenWithNoUserTest() {
//        // given
//        Long mockUserId = 1L;
//        QueueRequest queueRequest = new QueueRequest();
//        queueRequest.setUserId(mockUserId);
//
//        when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThrows(EntityNotFoundException.class, () -> queueService.issueQueueToken(queueRequest));
//    }

    @DisplayName("만료된 ACTIVE 토큰은 제거된다.")
    @Test
    void expireQueueToken_whenExpired_shouldRemoveToken() {
        // given
        String tokenValue = "expired-token-001";
        LocalDateTime now = LocalDateTime.now();
        QueueExpireRequest request = new QueueExpireRequest(
                1L, tokenValue, now.minusMinutes(10), now.minusMinutes(5), TokenStatus.ACTIVE
        );

        Queue expiredQueue = Queue.builder()
                .userId(1L)
                .tokenValue(tokenValue)
                .tokenStatus(TokenStatus.ACTIVE)
                .issuedAt(request.getIssuedAt())
                .expiredAt(request.getExpiredAt())
                .build();

        when(redisQueueRepository.findByTokenValueAndTokenStatus(tokenValue, TokenStatus.ACTIVE))
                .thenReturn(Optional.of(expiredQueue));

        // when
        ResponseMessage<QueueResponse> response = queueService.expireQueueToken(request);

        // then
        assertEquals("대기열 토큰이 만료되었습니다.", response.getMessage());
        assertEquals(TokenStatus.EXPIRED, response.getData().getTokenStatus());

        verify(redisQueueRepository).removeExpiredQueue(expiredQueue);
    }

    @DisplayName("유효한 ACTIVE 토큰은 제거되지 않는다.")
    @Test
    void expireQueueToken_whenNotExpired_shouldNotRemoveToken() {
        // given
        String tokenValue = "valid-token-002";
        LocalDateTime now = LocalDateTime.now();

        QueueExpireRequest request = new QueueExpireRequest(
                1L, tokenValue, now.minusMinutes(1),now.plusMinutes(4), TokenStatus.ACTIVE
        );

        Queue validQueue = Queue.builder()
                .userId(1L)
                .tokenValue(tokenValue)
                .tokenStatus(TokenStatus.ACTIVE)
                .issuedAt(request.getIssuedAt())
                .expiredAt(request.getExpiredAt())
                .build();

        when(redisQueueRepository.findByTokenValueAndTokenStatus(tokenValue, TokenStatus.ACTIVE))
                .thenReturn(Optional.of(validQueue));

        // when
        ResponseMessage<QueueResponse> response = queueService.expireQueueToken(request);

        // then
        assertEquals("대기열 토큰은 아직 유효합니다.", response.getMessage());
        assertEquals(TokenStatus.ACTIVE, response.getData().getTokenStatus());

        verify(redisQueueRepository, never()).removeExpiredQueue(any());
    }

    @DisplayName("존재하지 않거나 이미 만료된 토큰은 예외를 던진다.")
    @Test
    void expireQueueToken_whenNotFound_shouldThrowException() {
        // given
        String tokenValue = "missing-token-003";
        LocalDateTime now = LocalDateTime.now();

        QueueExpireRequest request = new QueueExpireRequest(
                1L, tokenValue, now.minusMinutes(10),now.minusMinutes(5), TokenStatus.ACTIVE
        );

        when(redisQueueRepository.findByTokenValueAndTokenStatus(tokenValue, TokenStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () ->
                queueService.expireQueueToken(request)
        );
    }


}
