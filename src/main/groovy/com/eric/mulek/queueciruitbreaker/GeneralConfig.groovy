package com.eric.mulek.queueciruitbreaker

import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.retry.annotation.EnableRetry

@EnableJms
@Configuration
@EnableRetry
class GeneralConfig {
    // empty class
}
