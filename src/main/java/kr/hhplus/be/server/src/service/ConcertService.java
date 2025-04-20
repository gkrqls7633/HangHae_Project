package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.ConcertSeat;
import kr.hhplus.be.server.src.domain.model.Seat;
import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import kr.hhplus.be.server.src.domain.repository.ConcertSeatRepository;
import kr.hhplus.be.server.src.interfaces.concert.ConcertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    private final ConcertSeatRepository concertSeatRepository;

    /**
     * @description콘서트 목록 전체를 조회합니다.
     * @param
     * @return 콘서트 목록 전체 리스트
     */
    public List<ConcertResponse> getConcertList() {

        try {
            List<Concert> concertList = concertRepository.findAll();
            return concertList.stream()
                    .map(concert -> ConcertResponse.builder()
                            .concertId(concert.getConcertId())
                            .name(concert.getName())
                            .price(concert.getPrice())
                            .date(concert.getDate())
                            .time(concert.getTime())
                            .location(concert.getLocation())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("콘서트 목록 조회 중 오류 발생", e);
            throw new RuntimeException("콘서트 목록 조회 실패");
        }
    }

    /**
     * @description 콘서트 예약 가능한 좌석 목록을 조회합니다.
     * @param concertId 콘서트 ID
     * @return 예약 가능한 좌석 목록을 포함한 콘서트 객체
     */

    public ConcertResponse getAvailableSeats(Long concertId) {

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("해당 concertId가 존재하지 않습니다 : " + concertId));

        List<Seat> seatTotalList = concertSeatRepository.findAllSeatsByConcertId(concertId);

        ConcertSeat concertSeat = new ConcertSeat();
        concertSeat.setConcert(concert);
        concertSeat.setSeats(seatTotalList);

        //예약 가능 좌석 filter
        List<Seat> availableSeats = concertSeat.getAvailableSeats();

        ConcertResponse concertResponse = ConcertResponse.builder()
                .concertId(concertId)
                .name(concert.getName())
                .price(concert.getPrice())
                .date(concert.getDate())
                .time(concert.getTime())
                .location(concert.getLocation())
                .seatList(availableSeats)
                .build();

        return concertResponse;
    }
}
