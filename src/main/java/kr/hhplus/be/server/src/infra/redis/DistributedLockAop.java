package kr.hhplus.be.server.src.infra.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.hhplus.be.server.src.infra.redis.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);  // (1)

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());  // (2)
            if (!available) {
                log.info("Waiting for lock for {} {}", kv("serviceName", method.getName()), kv("lockKey", key));

//                log.info("Failed to acquire lock for {} {}", kv("serviceName", method.getName()), kv("lockKey", key));
                return false;
            }

            log.info("Lock acquired for {} {}", kv("serviceName", method.getName()), kv("lockKey", key));

            // 트랜잭션에 락 해제 작업 동기화로 진행(비동기로 인한 락 해제와 트랜잭션 완료 격차 발생 방지)
            // 트랜잭션 커밋 후 락 해제
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    releaseLock(key, rLock);
                }
            });

            return joinPoint.proceed();  // (3)
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }
    }

    private void releaseLock(String key, RLock rLock) {
        try {
            if (rLock.isLocked()) {
                rLock.unlock();
                log.info("Lock released for key {}", key);
            }
        } catch (IllegalMonitorStateException e) {
            log.info("Redisson Lock Already Unlocked for key {}", key);
        }
    }

    private Map<String, Object> kv(String key, Object value) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(key, value);
        return logMap;
    }
}
