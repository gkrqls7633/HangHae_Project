package kr.hhplus.be.server.src.domain.booking.event;

import kr.hhplus.be.server.src.domain.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExternalDataSaveEvent {

    private Booking booking;
}
