package kr.hhplus.be.server.src.service.unit.schedular.point;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.src.common.ResponseMessage;
import kr.hhplus.be.server.src.domain.model.Point;
import kr.hhplus.be.server.src.domain.repository.PointRepository;
import kr.hhplus.be.server.src.interfaces.point.PointResponse;
import kr.hhplus.be.server.src.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = "spring.jpa.properties.hibernate.connection.isolation=1") // READ_UNCOMMITTED
class PointServiceTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testReadUncommittedDirtyRead() throws Exception {
        // Given
        Long userId = 1L;
        Point point = Point.builder()
                .userId(userId)
                .pointBalance(10000L)
                .build();
        pointRepository.save(point);
        entityManager.flush();
        entityManager.clear();

        // When
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        // 트랜잭션 1: 포인트 업데이트 (아직 커밋 안 함)
        Runnable updateTask = () -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
            transactionTemplate.execute(status -> {
                Point updatingPoint = pointRepository.findById(userId).orElseThrow();
                updatingPoint.setPointBalance(20000L);
                pointRepository.save(updatingPoint);
                entityManager.flush();
                entityManager.clear();
//                entityManager.clear(); // 캐시 제거
                latch.countDown();
                try {
                    Thread.sleep(500); // 커밋 지연
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                status.setRollbackOnly(); // 롤백
                return null;
            });
        };

        // 트랜잭션 2: 더티 리드 조회
        Future<ResponseMessage<PointResponse>> readTask = executor.submit(() -> {
            latch.await();
            entityManager.clear(); // 캐시 제거
            return pointService.getPoint(userId);
        });

        executor.submit(updateTask);

        // Then
        ResponseMessage<PointResponse> response = readTask.get();
        assertThat(response.getData().getPointBalance()).isEqualTo(20000L); // 더티 리드 확인

        // 최종 데이터 확인
        entityManager.clear();
        Point finalPoint = pointRepository.findById(userId).orElseThrow();
        assertThat(finalPoint.getPointBalance()).isEqualTo(10000L);

        executor.shutdown();
    }

    private PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManager.getEntityManagerFactory());
    }
}