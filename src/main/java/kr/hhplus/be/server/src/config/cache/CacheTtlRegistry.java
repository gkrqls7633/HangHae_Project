package kr.hhplus.be.server.src.config.cache;

import kr.hhplus.be.server.src.common.enums.CacheName;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CacheTtlRegistry {

    public Duration getTtlFor(String cacheName) {

        for (CacheName cache : CacheName.values()) {
            if (cache.getName().equals(cacheName)) {
                return cache.getTtl();
            }
        }
        return Duration.ofMinutes(10); // default TTL 10분
    }
}
