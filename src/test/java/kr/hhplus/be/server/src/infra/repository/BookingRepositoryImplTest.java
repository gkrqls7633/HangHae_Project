package kr.hhplus.be.server.src.infra.repository;

import kr.hhplus.be.server.src.domain.booking.BookingRepository;
import kr.hhplus.be.server.src.domain.concert.ConcertRepository;
import kr.hhplus.be.server.src.domain.user.User;
import kr.hhplus.be.server.src.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class BookingRepositoryImplTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    //테스트 컨테이너 활용 테스트
    @Test
    void saveBooking() {
        User user = new User();
        user.setUserName("김테스트");
        user.setPhoneNumber("010-1234-1234");
        user.setEmail("test2@naver.com");
        user.setAddress("서울특별시 강서구 등촌동");
        User savedUser = userRepository.save(user);

//        Concert concert = new Concert(1L, "BTS World Tour", 150000L, "2025-05-01", "19:00", "서울 올림픽 경기장");
//        Concert savedConcert = concertRepository.save(concert);
//
//        Booking booking = new Booking();
//        booking.setUser(savedUser);
//        booking.setConcert(concert);
//        booking.setSeatId(5L);
//        booking.setSeatNum(5L);
//        booking.setConcert(savedConcert);
//
//        bookingRepository.save(booking);
//
//        assertNotNull(booking.getBookingId());
    }

}