package kr.hhplus.be.server.src.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Schema(description = "콘서트 좌석 정보")
public class Seat {

    @Id
    @Schema(description = "좌석 ID", example = "1")
    private Long seatId;

    @ManyToOne
    @JsonIgnore  // JSON 직렬화 시 'concert' 필드를 제외시킴
    @JoinColumn(name = "concert_id")
    @Schema(description = "콘서트 ID", example = "1")
    private Concert concert;

    @Schema(description = "좌석 번호", example = "A1")
    private Long seatNum;

    @Schema(description = "좌석 상태", example = "AVAILABLE")
    private SeatStatus seatStatus;

    public Seat(Long seatId, Long seatNum, SeatStatus seatStatus) {
        this.seatId = seatId;
        this.seatNum = seatNum;
        this.seatStatus = seatStatus;
    }

    // 좌석의 예약 가능 상태를 반환
    public boolean isAvailable() {
        return this.seatStatus == SeatStatus.AVAILABLE;
    }

    //콘서트 예약 가능한 좌석을 조회한다.(createSeats에서 만들어진 좌석 중 SeatStatus가 AVAILABLE인 것만 조회)
    public List<Seat> getAvailableSeats(List<Seat> seatList) {
        return seatList.stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
    }


}