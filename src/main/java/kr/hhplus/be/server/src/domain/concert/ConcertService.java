package kr.hhplus.be.server.src.domain.concert;

import kr.hhplus.be.server.src.interfaces.concert.ConcertResponse;
import java.util.List;

public interface ConcertService {

    List<ConcertResponse> getConcertList();

    ConcertResponse getAvailableSeats(Long concertId);


}
