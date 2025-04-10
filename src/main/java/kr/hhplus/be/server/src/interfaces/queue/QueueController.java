package kr.hhplus.be.server.src.interfaces.queue;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController {

    private final QueueService queueService;

    // 예약 대기열 토큰 발급
    @Operation(summary = "예약 대기열 토큰 발급", description = "유저에게 예약 대기열 토큰을 발급합니다.")
    @PostMapping("/token")
    public ResponseMessage<QueueResponse> issueQueueToken(@RequestBody QueueRequest queueRequest) {

        return queueService.issueQueueToken(queueRequest);
    }

    //스케줄러로 구현하자.
    @Operation(summary = "예약 대기열 토큰 만료 스케줄러", description = "유저의 예약 대기 토큰을 만료시킨다.")
    @PostMapping("/token/expire")
    public ResponseMessage<String> expireQueueToken(@RequestBody QueueRequest queueRequest) {

        return queueService.expireQueueToken(queueRequest);
    }
}
