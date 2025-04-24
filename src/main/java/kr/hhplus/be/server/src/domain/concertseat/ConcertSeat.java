package kr.hhplus.be.server.src.domain.concertseat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.seat.Seat;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "콘서트좌석 도메인")
public class ConcertSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "콘서트 좌석 ID", example = "1")
    private Long concertSeatId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", referencedColumnName = "concertId")
    @JsonIgnore
    private Concert concert;

    @OneToMany(mappedBy = "concertSeat")
    @Schema(description = "이 콘서트의 좌석 목록")
    private List<Seat> seats;

    public static ConcertSeat of(Concert concert, List<Seat> seats) {
        return ConcertSeat.builder()
                .concert(concert)
                .seats(seats)
                .build();
    }

    public static ConcertSeat of(Concert concert) {
        return ConcertSeat.builder()
                .concert(concert)
                .build();
    }

    //콘서트 예약 가능한 좌석을 조회한다.(createSeats에서 만들어진 좌석 중 SeatStatus가 AVAILABLE인 것만 조회)
    public List<Seat> getAvailableSeats() {
        return this.seats.stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
    }

}
