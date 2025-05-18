package kr.hhplus.be.server.src.application.service;

import kr.hhplus.be.server.src.domain.booking.BookingRank;
import kr.hhplus.be.server.src.domain.booking.BookingRankingRepository;
import kr.hhplus.be.server.src.domain.concert.ConcertRankingService;
import kr.hhplus.be.server.src.interfaces.concert.dto.ConcertBookingRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ConcertRankingServiceImpl implements ConcertRankingService {

    private final BookingRankingRepository bookingRankingRepository;

    @Override
    public ConcertBookingRankResponse getConcertBookingRank() {

        BookingRank bookingRank = bookingRankingRepository.getConcertBookingRank();

        //BookRank -> RankDto
        return ConcertBookingRankResponse.builder()
                .rankings(bookingRank.toRankDtoList())
                .build();
    }
}
