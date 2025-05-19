package kr.hhplus.be.server.src.domain.booking.event;

import lombok.Getter;

@Getter
public class ConcertBookingScoreIncrementFailedEvent {
    private String concertId;
    private String errorMessage;  // 예외 메시지를 저장
    private String stackTrace;    // 예외의 스택 트레이스 저장

    public ConcertBookingScoreIncrementFailedEvent(String concertId, Exception e) {
        this.concertId = concertId;
        this.errorMessage = e.getMessage();
        this.stackTrace = getStackTraceAsString(e);
    }

    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
