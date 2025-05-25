package kr.hhplus.be.server.src.application.service.concert;

import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.concert.ConcertService;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeat;
import kr.hhplus.be.server.src.domain.concertseat.ConcertSeatRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertInfoResponse;
import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertRequest;
import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertResponse;
import kr.hhplus.be.server.src.interfaces.api.seat.dto.SeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;

    private final ConcertSeatRepository concertSeatRepository;

    private final SeatRepository seatRepository;

    private final BookingRankingRepository bookingRankingRepository;

    /**
     * @description콘서트 목록 전체를 조회합니다.
     * @param
     * @return 콘서트 목록 전체 리스트
     */
    @Cacheable(value = "concertList", key = "'concert:list:all'", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<ConcertInfoResponse> getConcertList() {

        try {
            List<Concert> concertList = concertRepository.findAll();
            return concertList.stream()
                    .map(concert -> ConcertInfoResponse.builder()
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
    @Override
    public ConcertResponse getAvailableSeats(Long concertId) {

        //콘서트 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("해당 concertId가 존재하지 않습니다 : " + concertId));

        //좌석 리스트 조회
        List<Seat> seatTotalList = concertSeatRepository.findAllSeatsByConcertId(concertId);

        //콘서트-좌석 매핑 정보
        ConcertSeat concertSeat = ConcertSeat.of(concert, seatTotalList);

        //예약 가능 좌석 filter
        List<Seat> availableSeats = concertSeat.getAvailableSeats();

        ConcertResponse concertResponse = ConcertResponse.builder()
                .concertId(concertId)
                .name(concert.getName())
                .price(concert.getPrice())
                .date(concert.getDate())
                .time(concert.getTime())
                .location(concert.getLocation())
                .seatList(
                    availableSeats.stream()
                        .map(SeatResponse::from)
                        .toList()
                )
                .build();

        return concertResponse;
    }

    @CacheEvict(value = "concertList", key = "'concert:list:all'")
    @Transactional
    @Override
    public ConcertResponse createConcert(ConcertRequest concertRequest) {

        // todo : 기존 콘서트와 중복되는거 체크할 수 있는 방법?
        Concert concert = Concert.builder()
                .price(concertRequest.getPrice())
                .name(concertRequest.getName())
                .date(concertRequest.getDate())
                .time(concertRequest.getTime())
                .location(concertRequest.getLocation())
                .build();

        //콘서트 정보 저장
        Concert createConcert = concertRepository.save(concert);

        ConcertSeat createConcertSeat = ConcertSeat.builder()
                .concert(createConcert)
                .build();

        //콘서트-좌석 정보 저장
        ConcertSeat saveConcertSeat = concertSeatRepository.save(createConcertSeat);

        int seatCnt = concertRequest.getSeatCnt();
        if (concertRequest.getSeatCnt() <= 0) {
            throw new IllegalArgumentException("콘서트의 좌석 수는 1개 이상이어야 합니다.");
        }
        List<Seat> createSeatList = Seat.createSeatList(seatCnt);
        createSeatList.forEach(seat -> {
            seat.setConcertSeat(saveConcertSeat);
        });

        //좌석 정보 저장
        seatRepository.saveAll(createSeatList);

        return ConcertResponse.builder()
                .concertId(createConcert.getConcertId())
                .name(createConcert.getName())
                .price(createConcert.getPrice())
                .date(createConcert.getDate())
                .time(createConcert.getTime())
                .location(createConcert.getLocation())
                .build();
    }

    @CacheEvict(value = "concertList", key = "'concert:list:all'")
    @Transactional
    @Override
    public ConcertResponse updateConcert(ConcertRequest concertRequest) {
        return null;
    }

    @Override
    public void cleanExpiredConcerts(LocalDateTime now) {
        List<Concert> expiredConcerts = concertRepository.findConcertsStartingBefore(LocalDateTime.now());

        for (Concert concert : expiredConcerts) {

            // Redis에서 해당 콘서트 제거
            bookingRankingRepository.cleanExpiredConcerts(concert.getConcertId().toString());
        }
    }

}
