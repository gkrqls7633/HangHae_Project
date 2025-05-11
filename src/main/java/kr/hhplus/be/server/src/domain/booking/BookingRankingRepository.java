package kr.hhplus.be.server.src.domain.booking;


import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface BookingRankingRepository {

    void incrementConcertBookingScore(String concertId);

    Set<ZSetOperations.TypedTuple<Object>> getConcertBookingRank();

}
