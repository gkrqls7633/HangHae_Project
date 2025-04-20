package kr.hhplus.be.server.src.domain.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "concert", indexes = {
        @Index(name = "idx_date", columnList = "date")
})
@Schema(description = "콘서트 도메안")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "콘서트 이름", example = "BTS World Tour")
    private String name;

    @Schema(description = "티켓 가격", example = "150000")
    private Long price;

    @Schema(description = "콘서트 날짜", example = "2025-05-01")
    private String date;

    @Schema(description = "콘서트 시간", example = "19:00")
    private String time;

    @Schema(description = "콘서트 장소", example = "서울 올림픽 경기장")
    private String location;

    @OneToOne(mappedBy = "concert")
    @Schema(description = "콘서트에 대한 좌석 목록")
    private ConcertSeat concertSeat;

    public Concert(Long concertId, String name, Long price, String date, String time, String location) {
        this.concertId = concertId;
        this.name = name;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
    }

}
