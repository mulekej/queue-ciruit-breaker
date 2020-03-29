package com.eric.mulek.queueciruitbreaker.circuitbreaker

import com.eric.mulek.queueciruitbreaker.MessagingCircuitBreakerEvent
import spock.lang.Specification

class ConsecutiveErrorThresholdSpec extends Specification {

    ConsecutiveErrorThreshold systemUnderTest

    void setup() {
        systemUnderTest = new ConsecutiveErrorThreshold(2)
    }

    void "If any successful event is received, the threshold is not met"() {
        given:
        MessagingCircuitBreakerEvent mockJmsApplicationEvent = Mock()
        mockJmsApplicationEvent.successful >> true

        expect:
        !systemUnderTest.thresholdIsMet(mockJmsApplicationEvent)
    }

    void "If all events in eventBuffer are failed, threshold is met"() {
        given:
        List<MessagingCircuitBreakerEvent> mockJmsApplicationEvents = (0..1).collect { Mock(MessagingCircuitBreakerEvent) }
        mockJmsApplicationEvents.each {
            it.successful >> false
        }

        systemUnderTest.thresholdIsMet(mockJmsApplicationEvents[0])

        expect:
        systemUnderTest.thresholdIsMet(mockJmsApplicationEvents[1])
    }

    void "eventBuffer size is maintained when more events than the window size are added"() {
        given:
        List<MessagingCircuitBreakerEvent> mockJmsApplicationEvents = (0..2).collect { Mock(MessagingCircuitBreakerEvent) }
        when:
        mockJmsApplicationEvents.each {
            systemUnderTest.thresholdIsMet(it)
        }

        then:
        systemUnderTest.eventBuffer.size() == 2
    }
}
