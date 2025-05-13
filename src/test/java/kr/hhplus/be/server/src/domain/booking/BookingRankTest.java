package kr.hhplus.be.server.src.domain.booking;

import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertBookingRankResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingRankTest {

    @DisplayName("BookingRank 도메인 엔터티의 dto builder 변환 메서드 테스트")
    @Test
    public void testToRankDtoList() {
        // given
        BookingRank bookingRank = new BookingRank(List.of(
                new BookingRank.Rank(1L, 10.0),
                new BookingRank.Rank(2L, 9.0),
                new BookingRank.Rank(3L, 8.0)
        ));

        bookingRank.getRankings().get(0).setRank(1);
        bookingRank.getRankings().get(1).setRank(2);
        bookingRank.getRankings().get(2).setRank(3);

        // when
        List<ConcertBookingRankResponse.RankDto> rankDtos = bookingRank.toRankDtoList();

        // then
        assertNotNull(rankDtos, "RankDto list should not be null");
        assertEquals(3, rankDtos.size(), "RankDto list should have 3 elements");

        ConcertBookingRankResponse.RankDto firstRankDto = rankDtos.get(0);
        assertEquals(1L, firstRankDto.getConcertId());
        assertEquals(10.0, firstRankDto.getScore());
        assertEquals(1, firstRankDto.getRank(), "First rank's rank should be 1");

        ConcertBookingRankResponse.RankDto secondRankDto = rankDtos.get(1);
        assertEquals(2L, secondRankDto.getConcertId());
        assertEquals(9.0, secondRankDto.getScore());
        assertEquals(2, secondRankDto.getRank(), "First rank's rank should be 1");

        ConcertBookingRankResponse.RankDto thirdRankDto = rankDtos.get(2);
        assertEquals(3L, thirdRankDto.getConcertId());
        assertEquals(8.0, thirdRankDto.getScore());
        assertEquals(3, thirdRankDto.getRank(), "First rank's rank should be 1");

    }

    @DisplayName("콘서트 예매 랭킹 sortedSet을 BookingRank 객체로 변환한다.")
    @Test
    void zSetToBookingRankTest() {

        // given
        ZSetOperations.TypedTuple<Object> tuple1 = mock(ZSetOperations.TypedTuple.class);
        when(tuple1.getValue()).thenReturn("1");
        when(tuple1.getScore()).thenReturn(5.0);

        ZSetOperations.TypedTuple<Object> tuple2 = mock(ZSetOperations.TypedTuple.class);
        when(tuple2.getValue()).thenReturn("2");
        when(tuple2.getScore()).thenReturn(3.0);

        Set<ZSetOperations.TypedTuple<Object>> rankingSet = new LinkedHashSet<>();
        rankingSet.add(tuple1);
        rankingSet.add(tuple2);

        // when
        BookingRank result = BookingRank.ZSetToBookingRank(rankingSet);

        // then
        assertNotNull(result);
        assertEquals(2, result.getRankings().size());

        BookingRank.Rank firstRank = result.getRankings().get(0);
        assertEquals(1L, firstRank.getConcertId());
        assertEquals(5.0, firstRank.getScore());
        assertEquals(1, firstRank.getRank());

        BookingRank.Rank secondRank = result.getRankings().get(1);
        assertEquals(2L, secondRank.getConcertId());
        assertEquals(3.0, secondRank.getScore());
        assertEquals(2, secondRank.getRank());
    }
}