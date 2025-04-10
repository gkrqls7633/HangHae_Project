package kr.hhplus.be.server.src.domain.model;

import kr.hhplus.be.server.src.interfaces.point.PointChargeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @DisplayName("유저 포인트 잔액이 결제할 가격보다 커야한다.")
    @Test
    void pointIsNotBiggerThanPrice() {

        //given
        Long mockUserId = 123L;

        Point point = new Point();
        point.setPointBalance(100000L); //유저의 포인트 100000원 mock 처리

        //콘서트 가격
        Concert concert = new Concert(mockUserId, "BTS World Tour", 150000L, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Long concertPrice = concert.getPrice();

        //when
        boolean result = point.isEnough(concertPrice);

        //then
        assertFalse(result);

    }

    @DisplayName("유저 포인트 잔액과 콘서트 가격이 동일하면 결제 가능이다.")
    @Test
    void pointIsSameConcertPrice() {

        //given
        Long mockUserId = 123L;

        Point point = new Point();
        point.setPointBalance(150000L); //유저의 포인트 150000원 mock 처리

        //콘서트 가격
        Concert concert = new Concert(mockUserId, "BTS World Tour", 150000L, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Long concertPrice = concert.getPrice();

        //when
        boolean result = point.isEnough(concertPrice);

        //then
        assertTrue(result);

    }

    @DisplayName("유저 포인트 잔액과 콘서트 가격이 동일하면 결제 가능이다.")
    @Test
    void pointIsBiggerThanConcertPrice() {

        //given
        Long mockUserId = 123L;

        Point point = new Point();
        point.setPointBalance(160000L); //유저의 포인트 160000뭔 mock 처리

        //콘서트 가격
        Concert concert = new Concert(mockUserId, "BTS World Tour", 150000L, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Long concertPrice = concert.getPrice();

        //when
        boolean result = point.isEnough(concertPrice);

        //then
        assertTrue(result);

    }

    @DisplayName("잔여 포인트 차감이 정상적으로 된다.")
    @Test
    void usePointTest() {

        //given
        Long mockUserId = 123L;
        Long mockConcertPrice = 150000L;

        Point point = new Point();
        point.setPointBalance(160000L); //유저의 포인트 160000원 mock 처리

        //콘서트 가격
        Concert concert = new Concert(mockUserId, "BTS World Tour", mockConcertPrice, "2025-05-01", "19:00", "서울 올림픽 경기장");
        Long concertPrice = concert.getPrice();

        //when
        Long result = point.usePoint(concertPrice);

        //then
        assertTrue(result >= 0);
        assertEquals(10000L, point.getPointBalance());
        assertEquals(point.getPointBalance(), result);
    }

    @DisplayName("결제 포인트가 부족한 경우 에러 발생한다.")
    @Test
    void usePointExceptionWithPointIsInsufficient() {
        Point point = new Point();
        point.setPointBalance(10000L);

        Long concertPrice = 150000L;

        assertThrows(IllegalStateException.class, () -> {
            point.usePoint(concertPrice);
        });
    }

    @DisplayName("포인트 충전이 성공한다.")
    @Test
    void chargePointTest() {

        //given
        Long mockUserId = 123L;

        PointChargeRequest pointChargeRequest = new PointChargeRequest();
        pointChargeRequest.setUserId(mockUserId);
        pointChargeRequest.setChargePoint(50000L);

        Point point = new Point();
        point.setPointBalance(50000L);

        //when
        point.chargePoint(pointChargeRequest);

        //then
        assertEquals(100000L, point.getPointBalance()); // 포인트 충전됐는지 확인

    }

    @DisplayName("포인트 충전 금액은 0보다 커야한다.")
    @Test
    void chargeMoreThanZeroPoint() {

        //given
        Long mockUserId = 123L;

        PointChargeRequest pointChargeRequest = new PointChargeRequest();
        pointChargeRequest.setUserId(mockUserId);
        pointChargeRequest.setChargePoint(0L);

        Point point = new Point();
        point.setPointBalance(50000L);


        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            point.chargePoint(pointChargeRequest);
        });

        assertEquals("충전 금액은 0 이상이어야 합니다.", exception.getMessage());

    }

}