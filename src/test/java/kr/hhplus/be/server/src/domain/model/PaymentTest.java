package kr.hhplus.be.server.src.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentTest {


    @DisplayName("결제 요청 좌석이 요청한 유저와 동일해야한다.")
    @Test
    void isSameUserIdWithPayReqUserId() {

        // given
        Long mockUserId = 123L; // 결제 요청의 userId
        Long mockBookingUserId = 123L; // 예약된 userId

        Booking booking = new Booking();
        booking.setUserId(mockBookingUserId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(mockUserId);

        //when
        boolean result = payment.isBookingCheck();

        //then
        assertTrue(result);

    }

    @DisplayName("결제 요청 좌석이 요청한 유저와 다르면 결제 실패한다.")
    @Test
    void isNotSameUserIdWithPayReqUserId() {

        // given
        Long mockUserId = 123L; // 결제 요청의 userId
        Long mockBookingUserId = 321L; // 예약된 userId

        Booking booking = new Booking();
        booking.setUserId(mockBookingUserId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(mockUserId);

        //when
        boolean result = payment.isBookingCheck();

        //then
        assertFalse(result);

    }

    @DisplayName("결제 요청 좌석에 userId가 점유되어 있지 않으면 결제 실패한다.")
    @Test
    void isNotOccupiedSeat() {

        // given
        Long mockUserId = 123L; // 결제 요청의 userId
        Long mockBookingUserId = null; // 예약된 userId 없음(null로 바뀜; 점유 해제됨)

        Booking booking = new Booking();
        booking.setUserId(mockBookingUserId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(mockUserId);

        //when
        boolean result = payment.isBookingCheck();

        //then
        assertFalse(result);

    }

}