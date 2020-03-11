package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public class ConsecutiveErrorThreshold implements JmsCircuitBreakerThreshold {

    private int consecutiveErrorThreshold;
    private List<JmsApplicationEvent> eventBuffer;

    public ConsecutiveErrorThreshold(int consecutiveErrorThreshold) {
        this.consecutiveErrorThreshold = consecutiveErrorThreshold;
        eventBuffer = new ArrayList<>();
    }

    @Override
    public boolean thresholdIsMet(JmsApplicationEvent event) {
        addEventToListAndMaintainWindowSize(event);
        return eventBuffer.stream().noneMatch(JmsApplicationEvent::isSuccessful);
    }

    private void addEventToListAndMaintainWindowSize(JmsApplicationEvent event) {
        eventBuffer.add(event);
        if (eventBuffer.size() > consecutiveErrorThreshold) {
            eventBuffer.remove(0);
        }
    }
}
