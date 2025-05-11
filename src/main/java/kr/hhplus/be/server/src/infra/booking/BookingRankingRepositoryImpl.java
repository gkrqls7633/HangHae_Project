package kr.hhplus.be.server.src.infra.booking;

import kr.hhplus.be.server.src.domain.booking.BookingRank;
import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BookingRankingRepositoryImpl implements BookingRankingRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String concertRankingKey = "concert:ranking";
    private static final double increment = 1.0;

    /**
     * @description RedisTemplate 활용하여 redis에 콘서트 랭킹 스코어를 저장합니다.
     *  - sorted set 자료구조로 저장
     *  - 예약 성공 시 score + 1
     * @param concertId 콘서트 ID
     */
    @Override
    public void incrementConcertBookingScore(String concertId) {

        redisTemplate.opsForZSet().incrementScore(concertRankingKey, concertId, increment);
    }

    /**
     * @description RedisTemplate 활용하여 redis에서 콘서트 매진 랭킹을 조회한다.(상위 10개 내림차순)
     */
    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getConcertBookingRank() {
        return redisTemplate.opsForZSet().reverseRangeWithScores(concertRankingKey, 0, 9);

    }
}
