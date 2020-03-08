package com.eric.mulek.queueciruitbreaker

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.listener.RetryListenerSupport
import org.springframework.retry.stats.DefaultStatisticsRepository
import org.springframework.retry.stats.StatisticsListener
import org.springframework.stereotype.Component


@Slf4j
@Component
class MyTestRetryService {

    @Autowired
    ExternalService externalService

    @Retryable(maxAttempts = 3, backoff = @Backoff(multiplier = 3D, delay = 1000L, maxDelay = 50000L))
    String getData(String val) {
        log.info('Primary Method')
        externalService.process()
    }

    @Recover
    String recovery(String val) {
        log.info('In recovery')
        'Recovery Value'
    }
}

@Component
class ExternalService {

    String process() {
        throw new RuntimeException('')
    }
}

@Slf4j
class MyFallbackListener extends RetryListenerSupport {

    @Override
    def <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.warn('An Error Occurred', throwable)
    }
}

@Configuration
class RetryConfig {

    @Bean
    StatisticsListener statisticsListener() {
        new StatisticsListener(new DefaultStatisticsRepository())
    }

    @Bean
    MyFallbackListener myFallbackListener() {
        new MyFallbackListener()
    }
}