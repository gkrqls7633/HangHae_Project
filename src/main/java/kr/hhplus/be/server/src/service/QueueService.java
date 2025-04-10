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
import kr.hhplus.be.server.src.interfaces.queue.QueueRequest;
import kr.hhplus.be.server.src.interfaces.queue.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ResponseMessage<QueueResponse> issueQueueToken(QueueRequest queueRequest) {

        // 1. 유저가 이미 발급받은 ACTIVE 상태의 토큰이 있는지 조회
        Optional<Queue> existingQueue = queueRepository.findByBooking_UserIdAndTokenStatus(queueRequest.getUserId(), TokenStatus.ACTIVE);

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

        QueueResponse queueResponse =  QueueResponse.builder()
                .tokenValue(queue.getTokenValue())
                .tokenStatus(queue.getTokenStatus())
                .issuedAt(queue.getIssuedAt())
                .expiredAt(queue.getExpiredAt())
                .bookId(queueRequest.getBookingId())
                .build();

        return ResponseMessage.success("대기열 토큰을 발급 완료했습니다.", queueResponse);
    }

    public ResponseMessage<String> expireQueueToken(QueueRequest queueRequest) {

        return ResponseMessage.success("대기열 토큰이 만료됐습니다.");

    }
}
