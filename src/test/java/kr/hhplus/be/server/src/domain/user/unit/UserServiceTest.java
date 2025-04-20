package kr.hhplus.be.server.src.domain.user.unit;

import kr.hhplus.be.server.src.application.service.UserServiceImpl;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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
}
