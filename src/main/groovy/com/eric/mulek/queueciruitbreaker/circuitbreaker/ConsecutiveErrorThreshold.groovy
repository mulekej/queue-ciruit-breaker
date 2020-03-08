package com.eric.mulek.queueciruitbreaker.circuitbreaker

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent

class ConsecutiveErrorThreshold implements JmsCircuitBreakerThreshold {

    private int consecutiveErrorThreshold = 5

    List<JmsApplicationEvent> eventBuffer

    @Override
    boolean thresholdIsMet(JmsApplicationEvent event) {
        addEventToListAndMaintainWindowSize(event)
        !eventBuffer.any { it.successful }
    }



    void addEventToListAndMaintainWindowSize(JmsApplicationEvent event) {
        eventBuffer << event
        if (eventBuffer.size() > consecutiveErrorThreshold) {
            eventBuffer.remove(0)
        }
    }
}
