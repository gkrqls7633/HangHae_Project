package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConcertService {

    //mock api 여부
    private static final String mockYsno = "Y";

    private final ConcertRepository concertRepository;

    /**
     * 콘서트 목록 전체를 조회합니다.
     *
     * @param
     * @return 콘서트 목록 전체 리스트
     */
    public List<Concert> getConcertList() {

        if (mockYsno.equals("Y")) {
            return  Arrays.asList(
                    new Concert(1L, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장"),
                    new Concert(2L, "IU Love Poem", 130000, "2025-06-10", "18:30", "부산 사직 경기장"),
                    new Concert(3L, "Coldplay Music of the Spheres", 180000, "2025-07-20", "20:00", "인천 아시아드 주경기장")
            );
        }
        return concertRepository.findAll();
    }

    /**
     * 콘서트 예약 가능한 좌석 목록을 조회합니다.
     *
     * @param concertId 콘서트 ID
     * @return 예약 가능한 좌석 목록을 포함한 콘서트 객체
     */
    public Concert getAvailableSeats(Long concertId) {

        if (mockYsno.equals("Y")) {

            //해당 콘서트의 좌석 상태 조회 예약가능 상태인 것만 조회
            Concert concert = new Concert(concertId, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장");
            List<Seat> availableSeatList = new Seat().getAvailableSeats(concert.getSeat());
            concert.setSeat(availableSeatList);

            return concert;
        }

        Optional<Concert> concert = concertRepository.findById(concertId);
        List<Seat> availableSeatList = new Seat().getAvailableSeats(concert.get().getSeat());
        concert.get().setSeat(availableSeatList);

        return concert.orElseThrow(() -> new RuntimeException("해당 concertId가 존재하지 않습니다 : " + concertId));
    }
}
