//package kr.hhplus.be.server.src.interfaces.controller.booking;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kr.hhplus.be.server.src.interfaces.booking.BookingController;
//import kr.hhplus.be.server.src.common.ResponseMessage;
//import kr.hhplus.be.server.src.domain.booking.Booking;
//import kr.hhplus.be.server.src.domain.concert.Concert;
//import kr.hhplus.be.server.src.domain.seat.Seat;
//import kr.hhplus.be.server.src.domain.model.enums.SeatStatus;
//import kr.hhplus.be.server.src.interfaces.booking.BookingResponse;
//import kr.hhplus.be.server.src.service.BookingService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BookingController.class)
//class BookingControllerTest {
//
//    @MockitoBean
//    private BookingService bookingService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @DisplayName("좌석 예약 테스트")
//    @Test
//    void bookingSeatTest() throws Exception {
//        // given
//        Map<String, SeatStatus> seatStatusMap = new HashMap<>();
//        seatStatusMap.put("1", SeatStatus.BOOKED);
//        seatStatusMap.put("3", SeatStatus.OCCUPIED);
//        seatStatusMap.put("4", SeatStatus.AVAILABLE);
//        seatStatusMap.put("5", SeatStatus.AVAILABLE);
//
//        //좌석 정보
//        Seat seat = new Seat();
////        seat.setConcertId(1L);
////        seat.setSeatStatusMap(seatStatusMap);
//
//        // 예약 정보
//        Booking booking = new Booking();
//        booking.setSeatNum("5");
//        booking.setConcert(new Concert(1L
//                , "BTS World Tour"
//                , 150000
//                , "2025-05-01"
//                , "19:00"
//                , "서울 올림픽 경기장"
////                , new Seat(1L, Map.of("1", SeatStatus.OCCUPIED)
//        )));
//
//        BookingResponse response = new BookingResponse(1L, "BTS World Tour", "4", "예약이 완료됐습니다.");
//
//        given(bookingService.bookingSeat(any(Booking.class)))
//                .willReturn(ResponseMessage.success("좌석 예약이 완료됐습니다.", response));
//
//        // when & then
//        mockMvc.perform(post("/booking")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(booking)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("좌석 예약이 완료됐습니다."))
//                .andExpect(jsonPath("$.data.seatNum").value("4"))
//                .andDo(print());
//
//        verify(bookingService).bookingSeat(any(Booking.class));
//
//    }
//
//    @DisplayName("점유된 좌석은 예약 요청 불가능하다.")
//    @Test
//    void bookingSeatWithOccupiedTest() {
//        //given
//        BookingController controller = new BookingController();
//
//        Concert concert = new Concert(1L
//                                    , "BTS World Tour"
//                                    , 150000
//                                    , "2025-05-01"
//                                    , "19:00"
//                                    , "서울 올림픽 경기장"
////                                    , new Seat(1L, Map.of("1", SeatStatus.OCCUPIED)
//        );
//
//        Booking booking = new Booking(1L, concert, "3");
//
//        // When
//        ResponseMessage<BookingResponse> response = controller.bookingSeat(booking);
//
//        //then
//        assertNotNull(response);
//        assertEquals(500, response.getStatus());
//        assertEquals("선택 좌석은 예약 불가능한 좌석입니다.", response.getMessage());
//    }
//
//
//}