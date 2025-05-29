package kr.hhplus.be.server.src.domain.queue.event;


public interface QueueEventPublisher {

    void success(QueueTokenIssuedEvent event);

    void processTokenIssueSuccess(QueuePromoteEvent event);

}
