package kr.hhplus.be.server.src.infra.booking;

import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingRankingRepositoryImplTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BookingRankingRepository bookingRankingRepository;

    @DisplayName("Redis에 concertId : score 가 sortedSet 형식으로 저장된다.")
    @Test
    void incrementConcertBookingScoreTest() {

        //given
        String concertId = "1";
        String key = "concert:ranking";

        redisTemplate.delete(key); // 초기화

        //when
        bookingRankingRepository.incrementConcertBookingScore(concertId);

        Double score = redisTemplate.opsForZSet().score(key, concertId);

        //then
        String type = redisTemplate.getConnectionFactory()
                .getConnection()
                .type(key.getBytes()).code();

        assertEquals("zset", type);
        assertEquals(1.0, score);
    }
}