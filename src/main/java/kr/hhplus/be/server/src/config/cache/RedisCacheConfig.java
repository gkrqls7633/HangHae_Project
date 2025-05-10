package kr.hhplus.be.server.src.config.cache;

import kr.hhplus.be.server.src.common.enums.CacheName;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final CacheTtlRegistry cacheTtlRegistry;

    public RedisCacheConfig(CacheTtlRegistry cacheTtlRegistry) {
        this.cacheTtlRegistry = cacheTtlRegistry;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {

        RedisSerializationContext.SerializationPair<String> stringSerializer = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
        RedisSerializationContext.SerializationPair<Object> jsonSerializer = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

        //커스텀 캐시 전용
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        for (CacheName cacheName : CacheName.values()) {
            cacheConfigs.put(
                    cacheName.getName(),
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(cacheTtlRegistry.getTtlFor(cacheName.getName()))
                            .serializeKeysWith(stringSerializer)
                            .serializeValuesWith(jsonSerializer)
            );
        }

        //default 캐시 전용 (CacheName enum에 정의되어 있지 않아도 기본 설정으로 캐시가 작동하도록 설정)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(stringSerializer)
                .serializeValuesWith(jsonSerializer);

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}