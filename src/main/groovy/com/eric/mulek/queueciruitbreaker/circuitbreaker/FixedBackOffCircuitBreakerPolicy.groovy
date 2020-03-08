package com.eric.mulek.queueciruitbreaker.circuitbreaker

import groovy.util.logging.Slf4j
import org.springframework.jms.config.JmsListenerEndpointRegistry
import java.util.concurrent.locks.ReentrantLock

@Slf4j
class FixedBackOffCircuitBreakerPolicy implements JmsCircuitBreakerPolicy {

    private static final ReentrantLock LOCK = new ReentrantLock()

    long backOffWindowInMilliseconds
    JmsListenerEndpointRegistry endpointRegistry

    @Override
    void process() {
        if (LOCK.tryLock()) {
            try {
                endpointRegistry.stop()
                Thread.sleep(backOffWindowInMilliseconds)
            } catch (InterruptedException e) {
                log.warn("event=JmsListenerSleepInterrupted", e)
            } finally {
                endpointRegistry.start()
                LOCK.unlock()
            }
        }
    }

    @Override
    void clear() {
        if (LOCK.tryLock()) {
            try {
                endpointRegistry.start()
            } finally {
                LOCK.unlock()
            }
        }
    }
}
