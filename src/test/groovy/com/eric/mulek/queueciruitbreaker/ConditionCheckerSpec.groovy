package com.eric.mulek.queueciruitbreaker

import spock.lang.Specification
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ConditionCheckerSpec extends Specification {

    List<Instant> eventBuffer = []
    int windowSize = 3
    long average


    void eric1() {
        given:
        (windowSize + 1).times {
            eventBuffer << Instant.from(LocalDate.now().minus(it, ChronoUnit.DAYS))
        }

        when:
        process()

        then:
        average == 0
    }

    void process() {
        int size = eventBuffer.size()
        def temp = []
        for (int position = size - 1; position >= (size - windowSize); position--) {
            temp << ChronoUnit.MILLIS.between(eventBuffer[position], eventBuffer[position - 1])
        }
    }
}
