package com.eric.mulek.queueciruitbreaker.circuitbreaker

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent
import spock.lang.Specification

class ConsecutiveErrorThresholdSpec extends Specification {

    ConsecutiveErrorThreshold systemUnderTest

    void setup() {
        systemUnderTest = new ConsecutiveErrorThreshold(2)
    }

    void "If any successful event is received, the threshold is not met"() {
        given:
        JmsApplicationEvent mockJmsApplicationEvent = Mock()
        mockJmsApplicationEvent.successful >> true

        expect:
        !systemUnderTest.thresholdIsMet(mockJmsApplicationEvent)
    }

    void "If all events in eventBuffer are failed, threshold is met"() {
        given:
        List<JmsApplicationEvent> mockJmsApplicationEvents = (0..1).collect { Mock(JmsApplicationEvent) }
        mockJmsApplicationEvents.each {
            it.successful >> false
        }

        systemUnderTest.thresholdIsMet(mockJmsApplicationEvents[0])

        expect:
        systemUnderTest.thresholdIsMet(mockJmsApplicationEvents[1])
    }

    void "eventBuffer size is maintained when more events than the window size are added"() {
        given:
        List<JmsApplicationEvent> mockJmsApplicationEvents = (0..2).collect { Mock(JmsApplicationEvent) }
        when:
        mockJmsApplicationEvents.each {
            systemUnderTest.thresholdIsMet(it)
        }

        then:
        systemUnderTest.eventBuffer.size() == 2
    }
}
