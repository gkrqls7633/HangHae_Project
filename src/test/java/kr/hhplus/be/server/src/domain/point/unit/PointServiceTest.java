package kr.hhplus.be.server.src.domain.point.unit;


import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.application.service.PointServiceImpl;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.point.Point;
import kr.hhplus.be.server.src.domain.point.PointRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.interfaces.point.dto.PointChargeRequest;
import kr.hhplus.be.server.src.interfaces.point.dto.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private PointRepository pointRepository;

    private Long userId;
    private Point mockPoint;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setUserName("김항해");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@naver.com");
        user.setAddress("서울특별시 강서구 염창동");

        userId = 1L;
        mockPoint = new Point();
        mockPoint.setUserId(userId);
        mockPoint.setUser(user);
        mockPoint.setPointBalance(1000L);
    }

    @Test
    @DisplayName("유저의 포인트 조회 테스트")
    void getPointTest() {

        //given
        when(pointRepository.findById(userId)).thenReturn(Optional.of(mockPoint));

        //when
        ResponseMessage<PointResponse> response = pointService.getPoint(userId);

        //then
        assertNotNull(response);
        assertEquals("포인트 잔액이 정상적으로 조회됐습니다.", response.getMessage());
        assertEquals(Optional.of(1000L).get(), response.getData().getPointBalance());

        verify(pointRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저의 포인트가 조회되지 않으면 EntityNotFound에러 발생")
    void doNotGetPointTest() {

        //given
        when(pointRepository.findById(userId)).thenReturn(Optional.empty());

        //when&then
        assertThrows(EntityNotFoundException.class, () -> pointService.getPoint(userId));

        verify(pointRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("유저의 포인트 충전 테스트")
    void chargePointtest() {

        //given
        PointChargeRequest mockPointChargeRequest = new PointChargeRequest();
        mockPointChargeRequest.setUserId(1L);
        mockPointChargeRequest.setChargePoint(100000L);

        when(pointRepository.findById(userId)).thenReturn(Optional.of(mockPoint));

        //when
        ResponseMessage<PointResponse> response = pointService.chargePoint(mockPointChargeRequest);

        //then
        assertNotNull(response);
        assertEquals("포인트가 정상적으로 충전됐습니다.", response.getMessage());
        assertEquals(Optional.of(101000L).get(), response.getData().getPointBalance());

        verify(pointRepository, times(1)).save(mockPoint);
    }


}
