package kr.hhplus.be.server.src.domain.concert;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcertTest {

    @Test
    void getConcertStartDate_shouldReturnCorrectLocalDateTime() {
        // given
        Concert concert = new Concert();
        concert.setConcertStartDate(LocalDateTime.of(2025, 5, 1, 19, 0));

        // when
        LocalDateTime result = concert.getConcertStartDate();

        // then
        LocalDateTime expected = LocalDateTime.of(2025, 5, 1, 19, 0);
        assertEquals(expected, result);
    }
}
