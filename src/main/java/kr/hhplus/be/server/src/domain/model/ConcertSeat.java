package kr.hhplus.be.server.src.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConcertSeat {

    @Id
    @Schema(description = "콘서트 좌석 ID", example = "1")
    private Long concertSeatId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", referencedColumnName = "concertId", insertable = false, updatable = false)
    @JsonIgnore
    private Concert concert;

    @OneToMany(mappedBy = "concertSeat")
    @Schema(description = "이 콘서트의 좌석 목록")
    private List<Seat> seats;

    //콘서트 예약 가능한 좌석을 조회한다.(createSeats에서 만들어진 좌석 중 SeatStatus가 AVAILABLE인 것만 조회)
    public List<Seat> getAvailableSeats() {
        return this.seats.stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
    }

}
