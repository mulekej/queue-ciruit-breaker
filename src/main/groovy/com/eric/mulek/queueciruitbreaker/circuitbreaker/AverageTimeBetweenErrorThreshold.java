package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class AverageTimeBetweenErrorThreshold implements JmsCircuitBreakerThreshold {

    private List<Instant> eventBuffer = new ArrayList<>();
    private int maxWindowSize = 3;
    private int minWindowSize = 2;
    private long threshold = 3000;

    public boolean thresholdIsMet(JmsApplicationEvent event) {
        addTimeStampToListAndMaintainWindowSize(event.getEventInstant());
        return eventBuffer.size() == minWindowSize && isThresholdSurpassed();
    }

    private boolean isThresholdSurpassed() {
        ArrayList<Long> temp = new ArrayList<>();
        for (int i = 0; i < eventBuffer.size() - 1;  i++) {
            temp.add(ChronoUnit.MILLIS.between(eventBuffer.get(i + 1), eventBuffer.get(i)));
        }
        Long sum = temp.stream().reduce(0L, Long::sum);
        return (sum / temp.size()) < threshold;
    }

    public void addTimeStampToListAndMaintainWindowSize(Instant instant) {
        eventBuffer.add(instant);
        if (eventBuffer.size() > maxWindowSize) {
            eventBuffer.remove(0);
        }
    }
}
