package com.eric.mulek.queueciruitbreaker;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import java.util.concurrent.locks.ReentrantLock;

@Configuration(proxyBeanMethods = false)
public class ExponentialBackoffConfig {

    @Bean
    @Qualifier("ExponentialLock")
    ReentrantLock reentrantLock() {
        return new ReentrantLock();
    }

    @Bean
    @Qualifier("ExponentialLock")
    ThreadHelper threadHelper() {
        return new ThreadHelper();
    }

    @Bean
    @Qualifier("ExponentialLock")
    ExponentialLockHandler exponentialLockHandler(@Value("${initial.wait.period.in.seconds:10}") int initialWaitPeriodInSeconds,
                                                  @Value("${max.wait.period.in.seconds:90}") int maxWaitPeriodInSeconds,
                                                  @Qualifier("ExponentialLock") ReentrantLock lock,
                                                  @Qualifier("ExponentialLock") ThreadHelper threadHelper,
                                                  JmsListenerEndpointRegistry endpointRegistry) {
        return new ExponentialLockHandler(initialWaitPeriodInSeconds, maxWaitPeriodInSeconds, lock, threadHelper, endpointRegistry);
    }

    @Bean
    @Qualifier("ExponentialLock")
    BackOffStartManager backOffStartManager(ExponentialLockHandler exponentialLockHandler,
                                            ConditionChecker conditionChecker) {
        return new BackOffStartManager(exponentialLockHandler, conditionChecker);
    }
}
