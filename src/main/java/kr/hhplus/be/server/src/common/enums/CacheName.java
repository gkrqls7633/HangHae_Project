package kr.hhplus.be.server.src.common.enums;

import java.time.Duration;

public enum CacheName {

    CONCERT_CACHE("concertCache", Duration.ofMinutes(60)),
    USER_POINT("userPoint", Duration.ofMinutes(10));

    private final String name;
    private final Duration ttl;

    CacheName(String name, Duration ttl) {
        this.name = name;
        this.ttl = ttl;
    }

    public String getName() {
        return name;
    }

    public Duration getTtl() {
        return ttl;
    }
}
