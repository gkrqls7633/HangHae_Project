package kr.hhplus.be.server.src.interfaces.concert.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConcertInfoResponse {
    private Long concertId;
    private String name;
    private Long price;
    private String date;
    private String time;
    private String location;
}
