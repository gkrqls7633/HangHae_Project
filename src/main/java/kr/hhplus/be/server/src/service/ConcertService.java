package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.interfaces.concert.ConcertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConcertService {

    //mock api 여부
    private static final String mockYsno = "Y";

    private final ConcertRepository concertRepository;

    /**
     * @description콘서트 목록 전체를 조회합니다.
     * @param
     * @return 콘서트 목록 전체 리스트
     */
    public List<ConcertResponse> getConcertList() {

        if (mockYsno.equals("Y")) {

            List<Concert> concertList = Arrays.asList(
                    new Concert(1L, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장"),
                    new Concert(2L, "IU Love Poem", 130000, "2025-06-10", "18:30", "부산 사직 경기장"),
                    new Concert(3L, "Coldplay Music of the Spheres", 180000, "2025-07-20", "20:00", "인천 아시아드 주경기장")
            );

            return concertList.stream()
                    .map(concert -> ConcertResponse.builder()
                            .concertId(concert.getConcertId())
                            .name(concert.getName())
                            .price(concert.getPrice())
                            .date(concert.getDate())
                            .time(concert.getTime())
                            .location(concert.getLocation())
//                            .concertSeat(new ConcertSeat())  // ConcertSeat는 적절한 객체로 설정 필요
                            .build())
                    .collect(Collectors.toList());
        }

        List<Concert> concertList = concertRepository.findAll();
        return concertList.stream()
                .map(concert -> ConcertResponse.builder()
                        .concertId(concert.getConcertId())
                        .name(concert.getName())
                        .price(concert.getPrice())
                        .date(concert.getDate())
                        .time(concert.getTime())
                        .location(concert.getLocation())
//                        .concertSeat(new ConcertSeat())  // ConcertSeat는 적절한 객체로 설정 필요
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * @description 콘서트 예약 가능한 좌석 목록을 조회합니다.
     * @param concertId 콘서트 ID
     * @return 예약 가능한 좌석 목록을 포함한 콘서트 객체
     */
    public ConcertResponse getAvailableSeats(Long concertId) {

        if (mockYsno.equals("Y")) {

            List<Seat> seatList = Arrays.asList(
                    new Seat(1L, 1L, SeatStatus.AVAILABLE),
                    new Seat(2L, 2L, SeatStatus.BOOKED),
                    new Seat(3L, 3L, SeatStatus.AVAILABLE),
                    new Seat(4L, 4L, SeatStatus.OCCUPIED)
            );

            ConcertSeat concertSeat = new ConcertSeat();
            List<Seat> availableSeat = concertSeat.getAvailableSeats(seatList);
            concertSeat.setSeats(availableSeat);

            return ConcertResponse.builder()
                    .concertId(concertId)
                    .name("BTS World Tour")
                    .price(150000)
                    .date("2024-05-01")
                    .time("19:00")
                    .location("서울 올림픽 경기장")
                    .concertSeat(concertSeat)
                    .build();
        }

        Optional<Concert> concertOpt = concertRepository.findById(concertId);
        Concert concert = concertOpt.orElseThrow(() -> new RuntimeException("해당 concertId가 존재하지 않습니다 : " + concertId));

        List<Seat> seatList = Arrays.asList(
                new Seat(1L, 1L, SeatStatus.AVAILABLE),
                new Seat(2L, 2L, SeatStatus.BOOKED),
                new Seat(3L, 3L, SeatStatus.AVAILABLE),
                new Seat(4L, 4L, SeatStatus.OCCUPIED)
        );

        ConcertSeat concertSeat = new ConcertSeat();
        List<Seat> availableSeat = concertSeat.getAvailableSeats(seatList);

        return ConcertResponse.builder()
                .concertId(concertId)
                .name("BTS World Tour")
                .price(150000)
                .date("2024-05-01")
                .time("19:00")
                .location("서울 올림픽 경기장")
                .concertSeat(concertSeat)
                .build();
    }
}
