package kr.hhplus.be.server.src.infra.booking;

import kr.hhplus.be.server.src.domain.booking.BookingRank;
import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

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
     * @description RedisTemplate 활용하여 redis에서 콘서트 매진 랭킹을 조회한다.(상위 n개 내림차순)
     */
    @Override
    public BookingRank getConcertBookingRank() {
        //SortedSet -> BbookRank
        Set<ZSetOperations.TypedTuple<Object>> zSet = redisTemplate.opsForZSet()
                .reverseRangeWithScores(concertRankingKey, 0, -1);

        return BookingRank.ZSetToBookingRank(zSet);
    }

    /**
     * @description redis에서 콘서트 매진 랭킹 sorted set의 특정 concertId를 제거한다.
     */
    @Override
    public void cleanExpiredConcerts(String concertId) {
        // Redis ZSet에서 해당 concertId의 순위를 확인
        Long rank = redisTemplate.opsForZSet().rank("concert:ranking", concertId);

        // 만약 해당 concertId가 ZSet에 존재하면
        if (rank != null) {
            redisTemplate.opsForZSet().remove("concert:ranking", concertId);
        }
    }

}
