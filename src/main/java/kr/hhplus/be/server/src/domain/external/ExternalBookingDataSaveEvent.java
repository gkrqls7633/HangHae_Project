package kr.hhplus.be.server.src.domain.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExternalBookingDataSaveEvent {

    private long bookingId;
    private long seatId;
    private long seatNum;
    private long concertId;

}