package kr.hhplus.be.server.src.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.common.enums.CommonEnumInterface;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;

@RequiredArgsConstructor
@Setter
@Getter
@Entity
@Schema(description = "좌석 도메인")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "좌석 ID", example = "1")
    private Long seatId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "concert_seat_id")
    @Schema(description = "콘서트좌석", example = "1")
    private ConcertSeat concertSeat;

    @Schema(description = "좌석 번호", example = "1")
    private Long seatNum;

    @Schema(description = "좌석 상태", example = "AVAILABLE")
    private SeatStatus seatStatus;

    public Seat(Long seatId, Long seatNum, SeatStatus seatStatus) {
        this.seatId = seatId;
        this.seatNum = seatNum;
        this.seatStatus = seatStatus;
    }

    public Seat(Long seatNum, SeatStatus seatStatus, ConcertSeat concertSeat) {
        this.seatNum = seatNum;
        this.seatStatus = seatStatus;
        this.concertSeat = concertSeat;
    }

    // 좌석의 예약 가능 상태를 반환
    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }


}