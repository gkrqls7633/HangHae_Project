package kr.hhplus.be.server.src.interfaces.concert;

import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConcertResponse {

    private Long concertId;
    private String name;
    private Long price;
    private String date;
    private String time;
    private String location;
    private ConcertSeat concertSeat;
}
