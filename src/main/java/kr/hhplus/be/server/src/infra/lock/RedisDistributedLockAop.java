package kr.hhplus.be.server.src.infra.lock;

import kr.hhplus.be.server.src.common.CustomSpringELParser;
import kr.hhplus.be.server.src.domain.lock.DistributedLockInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
public class RedisDistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final DistributedLockInterface distributedLockInterface;

    @Around("@annotation(kr.hhplus.be.server.src.infra.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        try {
            boolean lockAcquired = distributedLockInterface.acquireLock(key, distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!lockAcquired) {
                log.info("Waiting for lock for {} {}", kv("serviceName", method.getName()), kv("lockKey", key));
                return false;
            }

            log.info("Lock acquired for {} {}", kv("serviceName", method.getName()), kv("lockKey", key));

            // 트랜잭션에 락 해제 작업 동기화로 진행(비동기로 인한 락 해제와 트랜잭션 완료 격차 발생 방지)
            // 트랜잭션 커밋 후 락 해제
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    distributedLockInterface.releaseLock(key);
                }
            });

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }
    }

    private Map<String, Object> kv(String key, Object value) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(key, value);
        return logMap;
    }
}