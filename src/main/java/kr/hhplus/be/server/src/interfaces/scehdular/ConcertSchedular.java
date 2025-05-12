package kr.hhplus.be.server.src.interfaces.scehdular;

import kr.hhplus.be.server.src.domain.concert.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ConcertSchedular {

    private final ConcertService concertService;

    //스케줄 시간 정책 : 30분 주기로 콘서트 매진 랭킹 캐시 갱신
    @Scheduled(cron = "0 0/30 * * * *")
    public void cleanExpiredConcerts() {
        concertService.cleanExpiredConcerts(LocalDateTime.now());
    }

}
