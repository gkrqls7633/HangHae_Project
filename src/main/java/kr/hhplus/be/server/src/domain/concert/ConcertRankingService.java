package kr.hhplus.be.server.src.domain.concert;

import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertBookingRankResponse;

public interface ConcertRankingService {
    ConcertBookingRankResponse getConcertBookingRank();
}
