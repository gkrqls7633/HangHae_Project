package kr.hhplus.be.server.src.domain.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Builder
@Table(name = "seat", indexes = {
        @Index(name = "idx_concert_seat_num", columnList = "concert_seat_id, seat_num")
})
@Schema(description = "좌석 도메인")
public class Seat extends BaseTimeEntity {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", length = 10)
    private SeatStatus seatStatus;

//    @Version
//    private Long version;

    public Seat(long seatNum) {
        this.seatNum = seatNum;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    // 좌석의 예약 가능 상태를 반환
    @JsonIgnore
    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }

    // 좌석 점유 상태 변경
    public void changeBookedSeat() {
        this.setSeatStatus(SeatStatus.BOOKED);
    }

    // 좌석 리스트 신규 생성
    public static List<Seat> createSeatList(int seatCnt) {
        List<Seat> seatList = new ArrayList<>();
        for (long i = 1; i <= seatCnt; i++) {
            seatList.add(new Seat(i));
        }
        return seatList;
    }

}