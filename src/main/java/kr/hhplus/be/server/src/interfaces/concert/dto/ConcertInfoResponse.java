package kr.hhplus.be.server.src.interfaces.concert.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertInfoResponse {
    private Long concertId;
    private String name;
    private Long price;
    private String date;
    private String time;
    private String location;
}
