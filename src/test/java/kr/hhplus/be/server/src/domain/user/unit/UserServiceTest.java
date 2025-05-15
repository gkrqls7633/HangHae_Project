package kr.hhplus.be.server.src.domain.user.unit;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.application.service.UserServiceImpl;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.user.UserQueueRankResponse;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisQueueRepository redisQueueRepository;

    @Test
    @DisplayName("유저의 포인트를 조회한다.")
    void getUserPointTest() {

        //given
        Long mockUserId = 1L;

        User mockUser = User.builder()
                .userId(mockUserId)
                .userName("김테스트")
                .phoneNumber("010-1234-5678")
                .email("test2@naver.com")
                .address("서울특별시 강서구 등촌동")
                .point(Point.builder().userId(mockUserId).pointBalance(100000L).build())
                .build();

        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(mockUser));

        //when
        UserResponse response = userService.getUserPoint(mockUserId);

        //then
        assertEquals(Optional.of(100000L).get(), response.getPoint());

    }

    @Test
    @DisplayName("유저의 대기열 순번을 조회한다.")
    void getUserRankTest() {
        // given
        Long userId = 1L;
        String tokenValue = "590250b0-c9c7-4c1e-b855-28f57820c3";
        LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 15, 12, 56, 4, 305629000);
        Set<String> readyTokens = new LinkedHashSet<>(List.of(
                "token:123250b0-x9X7-4c1e-b855-28f57820c1",
                "token:590250b0-c9c7-4c1e-b855-28f57820c3",
                "token:590250b0-c9c7-4c1e-b855-28f578jw2a"
        ));

        when(redisQueueRepository.getUserTokenValue(userId)).thenReturn(tokenValue);
        when(redisQueueRepository.getReadyTokens(tokenValue, fixedTime)).thenReturn(readyTokens);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // when
            UserQueueRankResponse response = userService.getUserQueueRank(userId);

            // then
            assertEquals(2, response.getRank());
        }
    }

    @Test
    @DisplayName("유저 랭킹 조회 시 유저의 토큰이 대기열 내에 존재하지 않을 경우 에러 발생")
    void getUserQueueRank_tokenNotInQueue() {
        // given
        Long userId = 3L;
        String tokenValue = "notFoundToken";
        LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 15, 12, 56, 4, 305629000);

        Set<String> readyTokens = new LinkedHashSet<>(List.of("token:aaa", "token:bbb", "token:ccc"));

        when(redisQueueRepository.getUserTokenValue(userId)).thenReturn(tokenValue);
        when(redisQueueRepository.getReadyTokens(tokenValue, fixedTime)).thenReturn(readyTokens);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // when
            // then
            assertThrows(EntityNotFoundException.class, () -> userService.getUserQueueRank(userId));
        }
    }
}
