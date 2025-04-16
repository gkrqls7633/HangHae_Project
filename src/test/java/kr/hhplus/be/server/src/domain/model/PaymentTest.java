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
        User mockUser = new User();
        mockUser.setUserId(123L);

        User mockBookingUser = new User();
        mockBookingUser.setUserId(123L);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setUser(mockBookingUser);

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
        User mockUser = new User();
        mockUser.setUserId(123L);

        User mockBookingUser = new User();
        mockBookingUser.setUserId(321L);


        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setUser(mockBookingUser);

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
        User mockUser = new User();
        mockUser.setUserId(123L);

        User MockBookingUser = new User();
        MockBookingUser.setUserId(null);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setUser(MockBookingUser);

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
        User mockUser = new User();
        mockUser.setUserId(123L);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setUser(mockUser);

        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setUserId(123L);

        //when
        boolean result = payment.isBookingCheck(booking);

        //then
        assertTrue(result);

    }


}