package kr.hhplus.be.server.src.interfaces.booking;

import lombok.Getter;

// 예약 요청 request
@Getter
public class BookingRequest {

    private Long concertId;

    private Long seatId;

    private Long seatNum;

}
