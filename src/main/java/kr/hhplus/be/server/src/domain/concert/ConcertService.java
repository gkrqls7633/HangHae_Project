package kr.hhplus.be.server.src.domain.concert;

import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertInfoResponse;
import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertRequest;
import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertService {

    List<ConcertInfoResponse> getConcertList();

    ConcertResponse getAvailableSeats(Long concertId);

    ConcertResponse createConcert(ConcertRequest concertRequest);

    ConcertResponse updateConcert(ConcertRequest concertRequest);

    void cleanExpiredConcerts(LocalDateTime now);
}
