package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsListenerCircuitBreakerListener;
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
//    @ConditionalOnProperty(name = "", havingValue = "AverageTimeBetweenErrorThreshold")
    AverageTimeBetweenErrorThreshold averageTimeBetweenErrorThreshold() {
        return new AverageTimeBetweenErrorThreshold(1, 2, 3); //todo get from @value
    }

    @Bean
    @Qualifier("ExponentialLock")
    ConsecutiveErrorThreshold consecutiveErrorThreshold() {
        return new ConsecutiveErrorThreshold(5);
    }

    @Bean
    @Qualifier("ExponentialLock")
    ExponentialBackOffCircuitBreakerPolicy exponentialLockHandler(@Value("${initial.wait.period.in.seconds:10}") int initialWaitPeriodInSeconds,
                                                                  @Value("${max.wait.period.in.seconds:90}") int maxWaitPeriodInSeconds,
                                                                  @Qualifier("ExponentialLock") ReentrantLock lock,
                                                                  @Qualifier("ExponentialLock") ThreadHelper threadHelper,
                                                                  JmsListenerEndpointRegistry endpointRegistry) {
        return new ExponentialBackOffCircuitBreakerPolicy(initialWaitPeriodInSeconds, maxWaitPeriodInSeconds, lock, threadHelper, endpointRegistry);
    }

    @Bean
    @Qualifier("ExponentialLock")
    JmsListenerCircuitBreakerListener jmsListenerCircuitBreakerListener(MessagingCircuitBreakerPolicy exponentialLockHandler,
                                                                        MessagingCircuitBreakerThreshold threshold) {
        return new JmsListenerCircuitBreakerListener(exponentialLockHandler, threshold);
    }
}
