package kr.hhplus.be.server.src.infrastructure.repository;

import kr.hhplus.be.server.src.domain.model.Concert;

import java.util.List;

public interface ConcertRepositoryCustom {

    List<Concert> getConcertList();

}
