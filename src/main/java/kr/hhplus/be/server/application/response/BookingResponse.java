package kr.hhplus.be.server.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좌석 예약 응답")
public class BookingResponse {

    @Schema(description = "예약된 콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "예약된 콘서트 이름", example = "BTS World Tour")
    private String concertName;

    @Schema(description = "예약된 좌석 번호", example = "A12")
    private String seatNum;

    @Schema(description = "예약 상태 메시지", example = "좌석 예약이 완료됐습니다.")
    private String bookingMessage;

    public BookingResponse(Long concertId, String concertName, String seatNum, String bookingMessage) {
        this.concertId = concertId;
        this.concertName = concertName;
        this.seatNum = seatNum;
        this.bookingMessage = bookingMessage;
    }

    public Long getConcertId() {
        return concertId;
    }

    public String getConcertName() {
        return concertName;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public String getBookingMessage() {
        return bookingMessage;
    }

}
