package kr.hhplus.be.server.src.domain.queue;


public interface QueueTokenService {

    void processTokenIssue(Long userId, Long concertId);

    void processQueuePromote(Long userId, Long concertId);
}
