package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.locks.ReentrantLock;

public class ExponentialBackOffCircuitBreakerPolicy implements MessagingCircuitBreakerPolicy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int initialWaitPeriodInSeconds;
    private int maxWaitPeriodInSeconds;
    private int currentWaitPeriodInSeconds;
    private final ReentrantLock lock;
    private ThreadHelper threadHelper;
    private Lifecycle endpointRegistry;

    public ExponentialBackOffCircuitBreakerPolicy(int initialWaitPeriodInSeconds,
                                                  int maxWaitPeriodInSeconds,
                                                  ReentrantLock lock,
                                                  ThreadHelper threadHelper,
                                                  Lifecycle endpointRegistry) {
        this.initialWaitPeriodInSeconds = initialWaitPeriodInSeconds;
        this.maxWaitPeriodInSeconds = maxWaitPeriodInSeconds;
        this.lock = lock;
        this.threadHelper = threadHelper;
        this.endpointRegistry = endpointRegistry;

        currentWaitPeriodInSeconds = initialWaitPeriodInSeconds;
    }

    @Override
    @Async
    public void process() {
        if (lock.tryLock()) {
            try {
                endpointRegistry.stop();
                threadHelper.sleep(currentWaitPeriodInSeconds);
            } catch (InterruptedException e) {
                logger.warn("event=MessagingListenerSleepInterrupted", e);
            } finally {
                incrementWaitPeriod();
                endpointRegistry.start();
                lock.unlock();
            }
        }
    }

    private void incrementWaitPeriod() {
        int increasedWaitPeriod = currentWaitPeriodInSeconds * initialWaitPeriodInSeconds;
        currentWaitPeriodInSeconds = Math.min(maxWaitPeriodInSeconds, increasedWaitPeriod);
    }

    @Override
    public void clear() {
        if (lock.tryLock()) {
            try {
                currentWaitPeriodInSeconds = initialWaitPeriodInSeconds;
            } finally {
                lock.unlock();
            }
        }
    }
}
