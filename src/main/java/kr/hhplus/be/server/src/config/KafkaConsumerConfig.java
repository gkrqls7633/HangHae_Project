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
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    //Kafka Consumer를 생성하는 데 필요한 설정 정보를 담은 팩토리
    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz, String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); //key 역직렬화
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); //value 역직렬화

        // true : Kafka가 주기적으로 자동으로 커밋(최신 메시지만 처리하려는 경우에는 유리)
        // false: 수동 커밋 모드( 메시지 처리 성공 후에만 커밋하면, 장애 시 중복 처리는 있어도 유실은 없음)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        //Kafka에 현재 consumer group에 해당하는 offset이 없을 때 어디서부터 메시지를 읽을지를 결정
        //earliest : 가장 오래된 메시지부터 다시 읽기 시작 / latest : 가장 최근 메시지부터 읽기 시작
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        //한번에 최대 10개만 consume
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

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





    //@KafkaListener 사용할 수 있도록 셋팅 -> 리스너 메서드에서 사용
    //스프링이 @KafkaListener 어노테이션을 기반으로 메시지를 받을 수 있도록 리스너 컨테이너를 구성
    //위에서 만든 ConsumerFactory를 주입받아서 사용
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory); // KafkaConsumerFactory 주입 (역직렬화 및 서버 정보 등 포함)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); //수동 커밋 모드로 설정 (ack.acknowledge() 호출이 필요)
        factory.setConcurrency(3); // Consumer 스레드 수 3개

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
