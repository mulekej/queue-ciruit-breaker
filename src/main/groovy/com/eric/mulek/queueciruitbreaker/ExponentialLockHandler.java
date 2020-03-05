package com.eric.mulek.queueciruitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;

import java.util.concurrent.locks.ReentrantLock;

public class ExponentialLockHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BackOffExecution backOffExecution;
    private int initialWaitPeriodInSeconds;
    private int maxWaitPeriodInSeconds;
    private int currentWaitPeriodInSeconds;
    private final ReentrantLock lock;
    private ThreadHelper threadHelper;
    private JmsListenerEndpointRegistry endpointRegistry;

    public ExponentialLockHandler(int initialWaitPeriodInSeconds,
                                  int maxWaitPeriodInSeconds,
                                  BackOff backOff,
                                  ReentrantLock lock,
                                  ThreadHelper threadHelper,
                                  JmsListenerEndpointRegistry endpointRegistry) {
        this.initialWaitPeriodInSeconds = initialWaitPeriodInSeconds;
        this.maxWaitPeriodInSeconds = maxWaitPeriodInSeconds;
        this.lock = lock;
        this.threadHelper = threadHelper;
        this.endpointRegistry = endpointRegistry;

        backOffExecution = backOff.start();

        currentWaitPeriodInSeconds = initialWaitPeriodInSeconds;
    }

    @Async
    public void process() {
        if (lock.tryLock()) {
            try {
                endpointRegistry.stop();
                threadHelper.sleep(backOffExecution.nextBackOff());
            } catch (InterruptedException e) {
                logger.warn("event=JmsListenerSleepInterrupted", e);
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
