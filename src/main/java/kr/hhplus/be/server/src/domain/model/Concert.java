package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Schema(description = "콘서트 정보")
public class Concert {

    @Id
    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "콘서트 이름", example = "BTS World Tour")
    private String name;

    @Schema(description = "티켓 가격", example = "150000")
    private int price;

    @Schema(description = "콘서트 날짜", example = "2025-05-01")
    private String date;

    @Schema(description = "콘서트 시간", example = "19:00")
    private String time;

    @Schema(description = "콘서트 장소", example = "서울 올림픽 경기장")
    private String location;

    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "콘터스 좌석 정보", example = "")
    private List<Seat> seat;

    public List<Seat> createSeats(int count) {


        return range(1, count + 1)
                .mapToObj(i -> new Seat(
                        (long) i,
                        (long) i,
                        SeatStatus.AVAILABLE
                ))
                .collect(Collectors.toList());
    }

}
