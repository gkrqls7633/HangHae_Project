package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.model.Seat;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class ConcertRepositoryImpl implements ConcertRepositoryCustom {

    @Override
    public List<Concert> getConcertList() {

        List<Seat> seatList = new Concert().createSeats(50);

        return Arrays.asList(
                new Concert(1L, "BTS World Tour", 150000, "2024-05-01", "19:00", "서울 올림픽 경기장", seatList),
                new Concert(2L, "IU Love Poem", 130000, "2025-06-10", "18:30", "부산 사직 경기장", seatList),
                new Concert(3L, "Coldplay Music of the Spheres", 180000, "2025-07-20", "20:00", "인천 아시아드 주경기장", seatList)
        );
    }
}
