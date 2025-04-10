package kr.hhplus.be.server.src.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Schema(description = "콘서트 도메안")
public class Concert {

    @Id
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

    // 새로운 Concert 객체 생성 시, 자동으로 좌석을 50개 생성하여 seat 리스트에 넣어줌
    public Concert(Long concertId, String name, Long price, String date, String time, String location) {
        this.concertId = concertId;
        this.name = name;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
//        this.concertSeat = concertSeat;
    }

    //콘서트 좌석을 AVAILABLE로 50개 최초 생성
    private List<Seat> createSeats(int count) {
        return range(1, count + 1)
                .mapToObj(i -> new Seat(
                        (long) i,
                        (long) i,
                        SeatStatus.AVAILABLE
                ))
                .collect(Collectors.toList());
    }

    //콘서트 좌석을 랜덤 상태로 50개 생성
    private List<Seat> createRandomSeats(int count) {
        Random random = new Random();
        return range(1, count + 1)
                .mapToObj(i -> {
                    SeatStatus seatStatus = getRandomSeatStatus(random);
                    return new Seat(
                            (long) i,
                            (long) i,
                            seatStatus
                    );
                })
                .collect(Collectors.toList());
    }

    private SeatStatus getRandomSeatStatus(Random random) {
        int statusIdx = random.nextInt(3);
        return switch (statusIdx) {
            case 0 -> SeatStatus.AVAILABLE;
            case 1 -> SeatStatus.BOOKED;
            case 2 -> SeatStatus.OCCUPIED;
            default -> SeatStatus.AVAILABLE;
        };
    }
}
