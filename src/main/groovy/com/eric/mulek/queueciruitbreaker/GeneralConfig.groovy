package com.eric.mulek.queueciruitbreaker

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.retry.annotation.EnableRetry
import javax.jms.ConnectionFactory

@EnableJms
@Configuration
@EnableRetry
class GeneralConfig {

    @Bean
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory,
        MyErrorHandler myErrorHandler) {
        new DefaultJmsListenerContainerFactory().tap {
            configurer.configure(it, connectionFactory)
            it.setErrorHandler(myErrorHandler)
        }
    }
}
