package kr.hhplus.be.server.src.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Booking;
import kr.hhplus.be.server.src.domain.model.Queue;
import kr.hhplus.be.server.src.domain.model.User;
import kr.hhplus.be.server.src.domain.model.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.repository.BookingRepository;
import kr.hhplus.be.server.src.domain.repository.QueueRepository;
import kr.hhplus.be.server.src.domain.repository.UserRepository;
import kr.hhplus.be.server.src.interfaces.queue.QueueExpireRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    /**
     * @param queueRequest
     * @return
     * @description 신규 토큰 발급
     */
    public ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest) {

        // 1. 유저가 이미 발급받은 ACTIVE 상태의 토큰이 있는지 조회
        Optional<Queue> existingQueue = queueRepository.findByBookingUserIdAndTokenStatus(queueRequest.getUserId(), TokenStatus.ACTIVE);

        if (existingQueue.isPresent()) {
            Queue existingQueueObj = existingQueue.get();
            existingQueueObj.validateActiveToken(); // 유효한 토큰인지 확인 (예외 처리)
        }

        // 2. booking 정보를 조회해서 예약 정보가 있는지 체크 (예약 내역이 없으면 토큰 발급 x)
        Booking booking = bookingRepository.findById(queueRequest.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("예약 정보가 없습니다."));

        // 3. User 정보를 조회해서 해당 유저에 대한 정보를 가져옵니다.
        User user = userRepository.findById(queueRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        // 4. 토큰 발급
        Queue queue = new Queue();
        queue.setBooking(booking);
        queue.setUser(user);
        queue.newToken();

        queueRepository.save(queue);

        QueueResponse queueResponse = QueueResponse.builder()
                .tokenValue(queue.getTokenValue())
                .tokenStatus(queue.getTokenStatus())
                .issuedAt(queue.getIssuedAt())
                .expiredAt(queue.getExpiredAt())
                .bookId(queueRequest.getBookingId())
                .build();

        return ResponseMessage.success("대기열 토큰을 발급 완료했습니다.", queueResponse);
    }

    /**
     * @param queueExpireRequest
     * @return
     * @description controller에서 호출되는 단건으로 토큰 만료시키는 메서드 / 스케줄러에서 토큰 만료 호출하는 메서드
     */
    public ResponseMessage<QueueResponse> expireQueueToken(QueueExpireRequest queueExpireRequest) {

        // 1.userId, bookId로 발급된 유효한 상태의 토큰이 있는지 체크한다.
        Queue queue = queueRepository.findByBookingIdAndUserIdAndTokenStatus(
                queueExpireRequest.getUserId(),
                queueExpireRequest.getBookingId(),
                TokenStatus.ACTIVE);

        // 토큰이 없으면 종료
        if (queue == null) {
            return ResponseMessage.success("유효한 토큰이 존재하지 않습니다.", null);
        }

        // 2. 해당 토큰의 상태 만료, 만료시간 변경
        queue.setTokenStatus(TokenStatus.EXPIRED);
        queue.setExpiredAt(LocalDateTime.now());

        // 3. 토큰 만료 저장
        queueRepository.save(queue);

        QueueResponse queueResponse = QueueResponse.builder()
                .tokenValue(queue.getTokenValue())
                .tokenStatus(queue.getTokenStatus())
                .issuedAt(queue.getIssuedAt())
                .expiredAt(queue.getExpiredAt())
                .bookId(queueExpireRequest.getBookingId())
                .build();

        return ResponseMessage.success("대기열 토큰이 만료되었습니다.", queueResponse);

    }

    /**
     * @param queue
     * @return
     * @description 스케줄러에서 호출하는 토큰 만료 메서드
     */
    public ResponseMessage<QueueResponse> expireQueueToken(Queue queue) {
        // 1. 해당 토큰의 상태를 만료로 변경하고, 만료 시간 수정
        queue.setTokenStatus(TokenStatus.EXPIRED);
        queue.setExpiredAt(LocalDateTime.now());  // 만료 시간 수정

        // 3. 토큰 만료 저장
        queueRepository.save(queue);

        // 3. 성공 응답 반환
        QueueResponse queueResponse = QueueResponse.builder()
                .tokenValue(queue.getTokenValue())
                .tokenStatus(queue.getTokenStatus())
                .issuedAt(queue.getIssuedAt())
                .expiredAt(queue.getExpiredAt())
                .bookId(queue.getBooking().getBookingId())
                .build();

        return ResponseMessage.success("대기열 토큰이 만료되었습니다.", queueResponse);
    }
}
