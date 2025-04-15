package kr.hhplus.be.server.src.service.unit;

import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.user.UserResponse;
import kr.hhplus.be.server.src.service.UserService;
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
    private UserService userService;

    @Test
    @DisplayName("유저의 포인트를 조회한다.")
    void getUserPointTest() {

        //given
        Long mockUserId = 1L;

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(mockUserId);
        userResponse.setPoint(100000L);

        when(userRepository.getUserPoint(mockUserId)).thenReturn(userResponse);

        //when
        UserResponse response = userService.getUserPoint(mockUserId);

        //then
        assertEquals(Optional.of(100000L).get(), response.getPoint());


    }
}
