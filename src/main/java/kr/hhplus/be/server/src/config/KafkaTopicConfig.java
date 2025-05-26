package kr.hhplus.be.server.src.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * TopicBuilder란
 * - Spring Kafka에서 제공하는 토픽 생성을 위한 빌더 클래스입니다.
 * - Configuration 내에 bean 형태로 구성함으로써 서버가 실행될때, 지정한 Topic이 구성되고 생성이 됩니다
 * - 토픽의 다양한 설정(파티션 수, 복제 팩터, 설정 값 등)을 메서드 체이닝 방식으로 쉽게 구성할 수 있게 해줍니다.
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic seatBookedTopic() {
        return TopicBuilder.name("seat-booked-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //todo : 토픽 추가
//    @Bean
//    public NewTopic otherTopic() {
//        return TopicBuilder.name("other-topic")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
}
