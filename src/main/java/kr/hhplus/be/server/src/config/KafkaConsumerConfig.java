package kr.hhplus.be.server.src.config;

import kr.hhplus.be.server.src.domain.booking.event.SeatBookedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz, String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(clazz);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, SeatBookedEvent> seatBookedConsumerFactory() {
        return consumerFactory(SeatBookedEvent.class, "concert-consumer-group");
    }

    //todo : 컨슈머 추가 팩토리
//    @Bean
//    public ConsumerFactory<String, OtherEvent> otherConsumerFactory() {
//        return consumerFactory(OtherEvent.class, "other-consumer-group");
//    }

    //메시지 리스너의 동시성을 지원한다.
    //멀티스레드 환경에서 메시지를 효율적으로 처리할 수 있게 해준다.
    //각 리스너 컨테이너에 대한 세부적인 설정이 가능하다.

    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SeatBookedEvent> seatBookedListenerContainerFactory() {
        return kafkaListenerContainerFactory(seatBookedConsumerFactory());
    }

    //todo : 컨슈머 추가 리스너
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, OtherEvent> otherListenerContainerFactory() {
//        return kafkaListenerContainerFactory(otherConsumerFactory());
//    }
}
