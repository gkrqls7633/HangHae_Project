package kr.hhplus.be.server.src.domain.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@Entity
@Table(name = "concert", indexes = {
        @Index(name = "idx_date", columnList = "date")
})
@Schema(description = "콘서트 도메안")
public class Concert extends BaseTimeEntity {

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

    @Schema(description = "콘서트 시작 일자, 시간", example = "2025-05-01 19:00")
    private LocalDateTime concertStartDate;

    @Schema(description = "콘서트 장소", example = "서울 올림픽 경기장")
    private String location;

    @OneToOne(mappedBy = "concert")
    @Schema(description = "콘서트에 대한 좌석 목록")
    private ConcertSeat concertSeat;

    public Concert(String name, Long price, String date, String time, String location) {
//        this.concertId = concertId;
        this.name = name;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public Concert(String name, Long price, String concertStartDateTime, String location) {
        this.name = name;
        this.price = price;
        this.concertStartDate = LocalDateTime.parse(concertStartDateTime.replace(" ", "T"));  // "2025-05-01 19:00" -> "2025-05-01T19:00"
        this.location = location;
    }

    public LocalDateTime getStartDateTime() {
        LocalDate localDate = LocalDate.parse(this.date); // e.g. "2025-05-01"
        LocalTime localTime = LocalTime.parse(this.time); // e.g. "19:00"
        return LocalDateTime.of(localDate, localTime);
    }

}
