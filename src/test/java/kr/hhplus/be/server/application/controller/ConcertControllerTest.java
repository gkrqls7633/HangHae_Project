package kr.hhplus.be.server.application.controller;

import kr.hhplus.be.server.application.common.ResponseMessage;
import kr.hhplus.be.server.application.domain.Concert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConcertControllerTest {

    @DisplayName("콘서트 목록을 정상 조회한다")
    @Test
    void getConcertListTest() {

        //given
        ConcertController controller = new ConcertController();

        //when
        ResponseMessage<List<Concert>> response = controller.getConcertList();
        Concert firstConcert = response.getData().get(0);

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().size() >= 0);
    }

    @DisplayName("콘서트 에약 가능한 날짜를 조회한다.")
    @Test
    void getAvailableDateTest() {

        //given
        ConcertController controller = new ConcertController();

        //when
        ResponseMessage<String> response = controller.getAvailableDate();

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("예약 가능한 날짜가 정상적으로 조회됐습니다.", response.getMessage());
    }

}