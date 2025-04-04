package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제")
public class Payment {

    public Payment(String bookingId) {
        this.bookingId = bookingId;
    }

    @Schema(description = "예약id", example = "1")
    private String bookingId;

    public String getBookingId() {
        return bookingId;
    }

}
