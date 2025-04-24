package kr.hhplus.be.server.src.application.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.booking.Booking;
import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import kr.hhplus.be.server.src.domain.booking.BookingService;
import kr.hhplus.be.server.src.domain.concert.Concert;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.enums.SeatStatus;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.seat.Seat;
import kr.hhplus.be.server.src.domain.seat.SeatRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingRequest;
import kr.hhplus.be.server.src.interfaces.booking.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    //todo : facade로 분리 고민
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final QueueRepository queueRepository;

    @Override
    public ResponseMessage<BookingResponse> bookingSeatWithLock(BookingRequest bookingRequest) {
        try {
            return bookingSeat(bookingRequest);

        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("좌석 점유 실패: Optimistic Lock 예외 발생", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ResponseMessage<BookingResponse> bookingSeat(BookingRequest bookingRequest) {

        //활성화 상태의 토큰이 조회되지 않거나 || 해당 유효 기간(만료기간)이 현재시간보다 지난 경우 -> 서비스 불가
        Optional<Queue> activeToken = queueRepository.findByUserIdAndTokenStatus(bookingRequest.getUserId(), TokenStatus.ACTIVE);

        if (activeToken.isEmpty() || activeToken.get().getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("유효한 대기열 토큰이 존재하지 않습니다.");
        }

        // 1. concertId로 Concert 객체 조회 / userId로 UserId 객체 조회 / seat 정보 조회
        Concert concert = concertRepository.findById(bookingRequest.getConcertId())
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다."));

        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        Seat seat = seatRepository.findByConcertSeat_Concert_ConcertIdAndSeatNum(bookingRequest.getConcertId(), bookingRequest.getSeatNum())
                .orElseThrow(() -> new EntityNotFoundException("해당 좌석을 찾을 수 없습니다."));

        // 2. Booking 도메인 객체 생성
        Booking booking = new Booking(concert, bookingRequest.getSeatNum(), seat.getSeatId(), user);

        // 3. 예약 가능 여부 확인
        //  booking의 seatNum의 좌석 점유 여부 체크
        if (!booking.isAvailableBooking()) {
            return ResponseMessage.error(500, "선택 좌석은 예약 불가능한 좌석입니다.");

        } else {
            //좌석 점유 처리
            seat.setSeatStatus(SeatStatus.OCCUPIED);
            seatRepository.save(seat);

            // booking 예약 처리
            bookingRepository.save(booking);
        }

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setConcertId(concert.getConcertId());
        bookingResponse.setConcertName(concert.getName());
        bookingResponse.setSeatNum(seat.getSeatNum());

        return ResponseMessage.success("좌석 예약이 완료됐습니다.", bookingResponse);
    }

    @Transactional
    public ResponseMessage<BookingResponse> bookingSeatWithPessimisticLock(BookingRequest bookingRequest) {

        //활성화 상태의 토큰이 조회되지 않거나 || 해당 유효 기간(만료기간)이 현재시간보다 지난 경우 -> 서비스 불가
        Optional<Queue> activeToken = queueRepository.findByUserIdAndTokenStatus(bookingRequest.getUserId(), TokenStatus.ACTIVE);

        if (activeToken.isEmpty() || activeToken.get().getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("유효한 대기열 토큰이 존재하지 않습니다.");
        }

        // 1. concertId로 Concert 객체 조회 / userId로 UserId 객체 조회 / seat 정보 조회
        Concert concert = concertRepository.findById(bookingRequest.getConcertId())
                .orElseThrow(() -> new RuntimeException("해당 콘서트를 찾을 수 없습니다."));

        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보가 없습니다."));

        Seat seat = seatRepository.findByConcertIdAndSeatNumWithLock(bookingRequest.getConcertId(), bookingRequest.getSeatNum())
                .orElseThrow(() -> new EntityNotFoundException("해당 좌석을 찾을 수 없습니다."));

        // 2. Booking 도메인 객체 생성
        Booking booking = new Booking(concert, bookingRequest.getSeatNum(), seat.getSeatId(), user);

        // 3. 예약 가능 여부 확인
        //  booking의 seatNum의 좌석 점유 여부 체크
        if (!booking.isAvailableBooking()) {
            return ResponseMessage.error(500, "선택 좌석은 예약 불가능한 좌석입니다.");

        } else {
            //좌석 점유 처리
            seat.setSeatStatus(SeatStatus.OCCUPIED);
            seatRepository.save(seat);

            // booking 예약 처리
            bookingRepository.save(booking);
        }

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setConcertId(concert.getConcertId());
        bookingResponse.setConcertName(concert.getName());
        bookingResponse.setSeatNum(seat.getSeatNum());

        return ResponseMessage.success("좌석 예약이 완료됐습니다.", bookingResponse);
    }

}
