package com.eric.mulek.queueciruitbreaker

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit


class ConditionChecker2Spec {

    List<Instant> eventBuffer = []
    int windowSize = 3
    long average

    @Test
    void test1() {
        3.times {
            eventBuffer << LocalDateTime.now().minus(it, ChronoUnit.DAYS).toInstant(ZoneOffset.UTC)
        }

        def list = process()

        assert average == 0

    }

    List process() {
        int size = eventBuffer.size()
        def temp = []
        for (int position = size - 1; position > (size - windowSize); position--) {
            temp << ChronoUnit.MILLIS.between(eventBuffer[position], eventBuffer[position - 1])
        }
        def temp2 = []
        for (int i = 0; i < eventBuffer.size() - 1; i++) {
            temp2 << ChronoUnit.MILLIS.between(eventBuffer[i + 1], eventBuffer[i])
        }
        temp
    }

}