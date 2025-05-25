package kr.hhplus.be.server.src.interfaces.api.concert.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConcertBookingRankResponse {

    private List<RankDto> rankings;  // 콘서트 랭킹 리스트

    @Getter
    @Builder
    public static class RankDto {
        private long concertId; // 콘서트 ID
        private double score;   // 예약 점수
        private int rank;       // 순위
    }
}
