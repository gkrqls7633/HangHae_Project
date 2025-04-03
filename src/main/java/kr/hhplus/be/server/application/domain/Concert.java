package kr.hhplus.be.server.application.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "콘서트 정보")
public class Concert {

    @Schema(description = "콘서트 ID", example = "1")
    private Long concertId;

    @Schema(description = "콘서트 이름", example = "BTS World Tour")
    private String name;

    @Schema(description = "티켓 가격", example = "150000")
    private int price;

    @Schema(description = "콘서트 날짜", example = "2025-05-01")
    private String date;

    @Schema(description = "콘서트 시간", example = "19:00")
    private String time;

    @Schema(description = "콘서트 장소", example = "서울 올림픽 경기장")
    private String location;

    public Concert(Long concertId, String name, int price, String date, String time, String location) {
        this.concertId = concertId;
        this.name = name;
        this.price = price;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    public Long getConcertId() {
        return concertId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
}
