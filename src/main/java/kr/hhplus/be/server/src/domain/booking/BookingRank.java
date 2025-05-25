package kr.hhplus.be.server.src.domain.booking;

import kr.hhplus.be.server.src.interfaces.api.concert.dto.ConcertBookingRankResponse;
import lombok.*;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class BookingRank {

    private List<Rank> rankings;

    @Getter
    @Setter
    public static class Rank {
        private long concertId;
        private double score;
        private int rank;

        public Rank(long concertId, double score) {
            this.concertId = concertId;
            this.score = score;
        }

        // Rank -> RankDto 변환 메서드
        private ConcertBookingRankResponse.RankDto toRankDto() {
            return ConcertBookingRankResponse.RankDto.builder()
                    .concertId(this.concertId)
                    .score(this.score)
                    .rank(this.rank)
                    .build();
        }
    }

    // BookingRank -> ConcertBookingRankResponse로 변환 메서드
    public List<ConcertBookingRankResponse.RankDto> toRankDtoList() {
        return rankings.stream()
                .map(Rank::toRankDto)
                .collect(Collectors.toList());
    }

    // sorted set -> BookingRank 변환
    public static BookingRank ZSetToBookingRank(Set<ZSetOperations.TypedTuple<Object>> rankingSet) {

        List<BookingRank.Rank> rankings = new ArrayList<>();
        int currentRank = 1;

        if (rankingSet != null) {
            for (ZSetOperations.TypedTuple<Object> entry : rankingSet) {
                long concertId = Long.parseLong((String) entry.getValue());
                double score = entry.getScore();

                BookingRank.Rank rankEntry = new BookingRank.Rank(concertId, score);
                rankEntry.setRank(currentRank++);
                rankings.add(rankEntry);
            }
        }

        return new BookingRank(rankings);
    }
}


