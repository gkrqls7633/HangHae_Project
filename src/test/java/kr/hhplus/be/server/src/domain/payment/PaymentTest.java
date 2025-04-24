package kr.hhplus.be.server.src.domain.payment;

import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.enums.PaymentStatus;
import kr.hhplus.be.server.src.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentTest {

    @ParameterizedTest
    @CsvSource({
            "PENDING, COMPLETED",
            "PENDING, FAILED"
    })
    @DisplayName("결제 상태를 변경할 수 있다.")
    void changePaymentStatusTest(PaymentStatus initialStatus, PaymentStatus newStatus) {
        // given
        Payment payment = new Payment();
        payment.setPaymentStatus(initialStatus);

        // when
        payment.changePaymentStatus(newStatus);

        // then
        assertEquals(newStatus, payment.getPaymentStatus());
    }


    @DisplayName("결제 요청 좌석이 요청한 유저와 동일해야한다.")
    @Test
    void isSameUserIdWithPayReqUserId() {

        // given
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);

        User mockBookingUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockBookingUser.setUserId(123L);

        Booking booking = Booking.of(1L, mockBookingUser);

        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setUserId(123L);

        //when
        boolean result = payment.isBookingCheck(booking);

        //then
        assertTrue(result);
    }

    @DisplayName("결제 요청 좌석이 요청한 유저와 다르면 결제 실패한다.")
    @Test
    void isNotSameUserIdWithPayReqUserId() {

        // given
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);

        User mockBookingUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockBookingUser.setUserId(321L);

        Booking booking = Booking.of(1L, mockBookingUser);

        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setUserId(123L);

        //when
        boolean result = payment.isBookingCheck(booking);

        //then
        assertFalse(result);
    }

    @DisplayName("결제 요청 좌석에 userId가 점유되어 있지 않으면 결제 실패한다.")
    @Test
    void isNotOccupiedSeat() {

        // given
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);

        User mockBookingUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockBookingUser.setUserId(null);

        Booking booking = Booking.of(1L, mockBookingUser);

        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setUserId(123L);

        //when
        boolean result = payment.isBookingCheck(booking);

        //then
        assertFalse(result);

    }

    @DisplayName("예약내역 존재하는지 확인한다.")
    @Test
    void isBookingCheckTest() {

        //given
        User mockUser = User.of("김항해", "12345", "010-1234-5678", "test@naver.com", "서울특별시 강서구 염창동");
        mockUser.setUserId(123L);

        Booking booking = Booking.of(1L, mockUser);

        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setUserId(123L);

        //when
        boolean result = payment.isBookingCheck(booking);

        //then
        assertTrue(result);
    }

}