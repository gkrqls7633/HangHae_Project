package kr.hhplus.be.server.src.domain.booking;


public interface BookingRankingRepository {

    void incrementConcertBookingScore(String concertId);

    BookingRank getConcertBookingRank();

    void cleanExpiredConcerts(String concertId);
}
