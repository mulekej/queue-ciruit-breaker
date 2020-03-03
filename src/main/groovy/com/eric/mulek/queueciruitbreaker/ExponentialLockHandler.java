package com.eric.mulek.queueciruitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class ExponentialLockHandler {

    private static final ReentrantLock LOCK = new ReentrantLock();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int initialWaitPeriodInSeconds;
    private int maxWaitPeriodInSeconds;
    private int currentWaitPeriodInSeconds;
    private JmsListenerEndpointRegistry endpointRegistry;

    public ExponentialLockHandler(@Value("") int initialWaitPeriodInSeconds, @Value("") int maxWaitPeriodInSeconds, JmsListenerEndpointRegistry endpointRegistry) {
        this.initialWaitPeriodInSeconds = initialWaitPeriodInSeconds;
        this.maxWaitPeriodInSeconds = maxWaitPeriodInSeconds;
        this.endpointRegistry = endpointRegistry;

        currentWaitPeriodInSeconds = initialWaitPeriodInSeconds;
    }

    @Async
    public void process() {
        if (LOCK.tryLock()) {
            try {
                endpointRegistry.stop();
                //todo kick this out to a super simple helper class to aid in testing
                Thread.sleep(currentWaitPeriodInSeconds*1000);
            } catch (InterruptedException e) {
                logger.warn("event=JmsListenerSleepInterrupted", e);
            } finally {
                incrementWaitPeriod();
                endpointRegistry.start();
                LOCK.unlock();
            }
        }
    }

    private void incrementWaitPeriod() {
        int temp = currentWaitPeriodInSeconds * initialWaitPeriodInSeconds;
        currentWaitPeriodInSeconds = Math.min(maxWaitPeriodInSeconds, temp);
    }

    public void clear() {
        if (LOCK.tryLock()) {
            try {
                currentWaitPeriodInSeconds = initialWaitPeriodInSeconds;
            } finally {
                LOCK.unlock();
            }
        }
    }

    //for test purposes only, locks are touchy about being accessed outside of their scope
    void callLock() {
        LOCK.tryLock();
    }
}
