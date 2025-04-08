package kr.hhplus.be.server.src.service;

import kr.hhplus.be.server.src.domain.model.Concert;
import kr.hhplus.be.server.src.domain.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ConcertService {

    //mock api 여부
    private static final String mockYsno = "Y";

    private final ConcertRepository concertRepository;

    public List<Concert> getConcertList() {

        if (mockYsno.equals("Y")) {
            return concertRepository.getConcertList();
        }
        return concertRepository.findAll();

    }
}
