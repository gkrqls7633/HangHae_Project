package kr.hhplus.be.server.src.domain.queue.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenIssuedEvent {

    private Long userId;

    private Long concertId;
}
