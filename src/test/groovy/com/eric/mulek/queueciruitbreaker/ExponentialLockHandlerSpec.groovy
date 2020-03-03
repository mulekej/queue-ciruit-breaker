package com.eric.mulek.queueciruitbreaker

import org.springframework.jms.config.JmsListenerEndpointRegistry
import spock.lang.Specification

class ExponentialLockHandlerSpec extends Specification {

    ExponentialLockHandler systemUnderTest

    int testInitialWaitPeriodInSeconds = 99
    int testMaxWaitPeriodInSeconds = 66
    JmsListenerEndpointRegistry mockJmsListenerEndpointRegistry = Mock()

    void setup() {
        systemUnderTest = new ExponentialLockHandler(testInitialWaitPeriodInSeconds, testMaxWaitPeriodInSeconds, mockJmsListenerEndpointRegistry)
    }

    void "currentWaitPeriodInSeconds get set to the initialWaitPeriodInSeconds by the constructor"() {
        expect:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds
    }

    void "Calling clear resets the currentWaitPeriodInSeconds back to its initial value"() {
        given:
        systemUnderTest.currentWaitPeriodInSeconds = 1000

        when:
        systemUnderTest.clear()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds
    }

    void "Calling clear does nothing when lock is unavailable"() {
        given:
        systemUnderTest.currentWaitPeriodInSeconds = 1000
        Thread.start { systemUnderTest.callLock() }

        when:
        systemUnderTest.clear()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == 1000
    }

    void "test process()"() {
        when:
        def result = systemUnderTest

        then:
        !result
    }
}
