package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.MessagingCircuitBreakerEvent;

import java.util.ArrayList;
import java.util.List;

public class ConsecutiveErrorThreshold implements MessagingCircuitBreakerThreshold {

    private int consecutiveErrorThreshold;
    private List<MessagingCircuitBreakerEvent> eventBuffer;

    public ConsecutiveErrorThreshold(int consecutiveErrorThreshold) {
        this.consecutiveErrorThreshold = consecutiveErrorThreshold;
        eventBuffer = new ArrayList<>();
    }

    @Override
    public boolean thresholdIsMet(MessagingCircuitBreakerEvent event) {
        addEventToListAndMaintainWindowSize(event);
        return eventBuffer.stream().noneMatch(MessagingCircuitBreakerEvent::isSuccessful);
    }

    private void addEventToListAndMaintainWindowSize(MessagingCircuitBreakerEvent event) {
        eventBuffer.add(event);
        if (eventBuffer.size() > consecutiveErrorThreshold) {
            eventBuffer.remove(0);
        }
    }
}
