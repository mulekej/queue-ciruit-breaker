package com.eric.mulek.queueciruitbreaker

import com.eric.mulek.queueciruitbreaker.circuitbreaker.ExponentialBackoffConfig
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jms.config.JmsListenerEndpointRegistry
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class MyEventListenerIntegrationSpec extends Specification {

    @Autowired
    MyEventListener systemUnderTest

    @SpringBean
    JmsListenerEndpointRegistry mockJmsListenerEndpointRegistry = Mock()

    void "Name"() {
        when:
        systemUnderTest.process()

        then:
        false
    }
}

@Configuration
@Import(ExponentialBackoffConfig)
class TestConfig {

    @Bean
    ExamplePointCut examplePointCut() {
        new ExamplePointCut()
    }

    @Bean
    MyEventListener myEventListener() {
        new MyEventListener()
    }
}
