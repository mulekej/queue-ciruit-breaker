package com.eric.mulek.queueciruitbreaker

import org.springframework.jms.config.JmsListenerEndpointRegistry
import spock.lang.Specification
import java.util.concurrent.locks.ReentrantLock

class ExponentialLockHandlerSpec extends Specification {

    ExponentialLockHandler systemUnderTest

    ReentrantLock mockLock = Mock()
    ThreadHelper mockThreadHelper = Mock()
    JmsListenerEndpointRegistry mockEndpointRegistry = Mock()

    int testInitialWaitPeriodInSeconds = 10
    int testMaxWaitPeriodInSeconds = 10000

    void setup() {
        systemUnderTest = new ExponentialLockHandler(
            testInitialWaitPeriodInSeconds,
            testMaxWaitPeriodInSeconds,
            mockLock,
            mockThreadHelper,
            mockEndpointRegistry)
    }

    void "currentWaitPeriodInSeconds get set to the initialWaitPeriodInSeconds by the constructor"() {
        expect:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds
    }

    void "Calling clear resets the currentWaitPeriodInSeconds back to its initial value"() {
        given:
        systemUnderTest.currentWaitPeriodInSeconds = 1000
        mockLock.tryLock() >> true

        when:
        systemUnderTest.clear()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds
        1 * mockLock.unlock()
    }

    void "Calling clear does nothing when lock is unavailable"() {
        given:
        systemUnderTest.currentWaitPeriodInSeconds = 1000
        mockLock.tryLock() >> false

        when:
        systemUnderTest.clear()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == 1000
        0 * mockLock.unlock()
    }

    void "Should run through the happy path process method"() {
        given:
        mockLock.tryLock() >> true

        when:
        systemUnderTest.process()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds * testInitialWaitPeriodInSeconds
        1 * mockEndpointRegistry.stop()
        1 * mockEndpointRegistry.start()
        1 * mockThreadHelper.sleep(testInitialWaitPeriodInSeconds * 1000)
        1 * mockLock.unlock()
    }

    void "Process is a no-op when the lock isn't retrieved"() {
        given:
        mockLock.tryLock() >> false

        when:
        systemUnderTest.process()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == testInitialWaitPeriodInSeconds
        0 * mockEndpointRegistry._
        0 * mockLock.unlock()
        0 * mockThreadHelper._
    }

    void "If the incremented wait period exceeds the max wait period, the the current wait period the the max value"() {
        given:
        mockLock.tryLock() >> true
        systemUnderTest.maxWaitPeriodInSeconds = 90

        when:
        systemUnderTest.process()

        then:
        systemUnderTest.currentWaitPeriodInSeconds == 90
    }
}
