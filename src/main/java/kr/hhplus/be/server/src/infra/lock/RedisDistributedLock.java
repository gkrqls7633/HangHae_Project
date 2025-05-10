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

    /* tryLock 내부 흐름
    -- Redisson은 Redis의 Pub/Sub 기능을 활용하여 락 해제를 감지하고, 다음 대기 중인 스레드(또는 인스턴스)가 즉시 락을 시도할 수 있도록 설계되어 있음.
    -- waittime = 0 으로 하는 경우 재시도 안하므로 simpleLock / waittime > 0 이면 재시도 하므로 pubsub 기반 Lock
    ---------------------------------------------------------------------------------------------------------------------------
    - Redis에 락을 요청하려고 시도
    - 이미 락이 점유 중이므로 즉시 실패하지 않고 대기 모드로 진입
    - Redisson 내부에서 해당 락 key의 Pub/Sub 채널을 Subscribe
    - 락 해제 이벤트를 수신 대기하며 블로킹 상태로 전환
    - 이때 CPU를 낭비하지 않음 (스핀락이 아님)
    - Redisson은 Redis에 unlock 명령어와 함께, 락 key에 해당하는 채널에 "unlock 이벤트"를 Publish함
    - 2번 스레드가 Subscribe 중인 채널에서 이 이벤트를 수신
    - 이벤트를 수신한 즉시, Redisson 내부에서 락 획득 재시도 로직을 실행
    - 이 시점에서 waitTime이 남아 있다면, 락 획득 시도 가능
    - 락 획득 성공 시 true 반환, 실패하면 계속 대기하거나 waitTime 만료 후 false 반환
    ---------------------------------------------------------------------------------------------------------------------------
     */
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
