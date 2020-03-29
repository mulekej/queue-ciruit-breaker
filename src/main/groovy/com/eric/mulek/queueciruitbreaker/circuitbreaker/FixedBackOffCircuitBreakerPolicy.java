package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import java.util.concurrent.locks.ReentrantLock;

public class FixedBackOffCircuitBreakerPolicy implements MessagingCircuitBreakerPolicy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReentrantLock lock;
    private long backOffWindowInMilliseconds;
    private Lifecycle endpointRegistry;
    private ThreadHelper threadHelper;

    public FixedBackOffCircuitBreakerPolicy(ReentrantLock lock, long backOffWindowInMilliseconds, Lifecycle endpointRegistry, ThreadHelper threadHelper) {
        this.lock = lock;
        this.backOffWindowInMilliseconds = backOffWindowInMilliseconds;
        this.endpointRegistry = endpointRegistry;
        this.threadHelper = threadHelper;
    }

    @Override
    public void process() {
        if (lock.tryLock()) {
            try {
                endpointRegistry.stop();
                threadHelper.sleep(backOffWindowInMilliseconds);
            } catch (InterruptedException e) {
                logger.warn("event=MessagingListenerSleepInterrupted", e);
            } finally {
                endpointRegistry.start();
                lock.unlock();
            }
        }
    }

    @Override
    public void clear() {
        if (lock.tryLock()) {
            try {
                endpointRegistry.start();
            } finally {
                lock.unlock();
            }
        }
    }
}
