package kr.hhplus.be.server.src.domain.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLockInterface {

    boolean acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit);

    void releaseLock(String key);

}
