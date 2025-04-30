package kr.hhplus.be.server.src.infra.lock;

import kr.hhplus.be.server.src.domain.lock.DistributedLockInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class RedisDistributedLock implements DistributedLockInterface {

    private final RedissonClient redissonClient;

    @Override
    public boolean acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(key);
        try {
            return rLock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock", e);
        }
    }

    @Override
    public void releaseLock(String key) {
        RLock rLock = redissonClient.getLock(key);
        try {
            if (rLock.isLocked()) {
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.info("Redisson Lock Already Unlocked for key {}", key);
        }
    }
}
