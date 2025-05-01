package kr.hhplus.be.server.src.domain.seat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import kr.hhplus.be.server.src.domain.BaseTimeEntity;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingCancelRequest;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//@RequiredArgsConstructor
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

    @Version
    private Long version;

    // 좌석의 예약 가능 상태를 반환
    @JsonIgnore
    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }

    public void changeBookedSeat() {
        this.setSeatStatus(SeatStatus.BOOKED);
    }

    //좌석 리스트 신규 생성
    //todo : 좌석 정보 만들어서 저장
    public static List<Seat> createSeatList() {
        List<Seat> seatList = new ArrayList<>();
        return seatList;
    }

}