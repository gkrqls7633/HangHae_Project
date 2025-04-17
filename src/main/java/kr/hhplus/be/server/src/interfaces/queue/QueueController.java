package kr.hhplus.be.server.src.interfaces.queue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대기열", description = "대기열 관리 API")
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
    @Operation(
            summary = "예약 대기열 토큰 만료", description = "n분 단위(임시)로 유저의 만료된 토큰을 만료시킨다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QueueExpireRequest.class),
                            examples = @ExampleObject(
                                name = "Minimal Request",
                                summary = "필수값만 포함된 요청 예시",
                                value = """
                                    {
                                      "userId": 123
                                    }
                                    """
                            )
                    )
            )
    )
    @PostMapping("/token/expire")
    public ResponseMessage<QueueResponse> expireQueueToken(@RequestBody QueueExpireRequest queueExpireRequest) {

        return queueService.expireQueueToken(queueExpireRequest);
    }
}
