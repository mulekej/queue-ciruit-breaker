package com.eric.mulek.queueciruitbreaker.circuitbreaker

import com.eric.mulek.queueciruitbreaker.MessagingCircuitBreakerEvent
import spock.lang.Specification

class AverageTimeBetweenErrorThresholdSpec extends Specification {

    AverageTimeBetweenErrorThreshold systemUnderTest

    void setup() {
        systemUnderTest = new AverageTimeBetweenErrorThreshold(3,2,1000)
    }

    void "successful event not added to buffer"() {
        given:
        MessagingCircuitBreakerEvent mockJmsApplicationEvent = Mock()
        mockJmsApplicationEvent.successful >> true

        when:
        systemUnderTest.thresholdIsMet(mockJmsApplicationEvent)

        then:
        !systemUnderTest.eventBuffer
    }
}
