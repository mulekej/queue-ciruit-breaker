package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AverageTimeBetweenErrorThreshold implements JmsCircuitBreakerThreshold {

    private int maxWindowSize;
    private int minWindowSize;
    private long threshold;
    private List<Instant> eventBuffer = new ArrayList<>();

    public AverageTimeBetweenErrorThreshold(int maxWindowSize, int minWindowSize, long threshold) {
        this.maxWindowSize = maxWindowSize;
        this.minWindowSize = minWindowSize;
        this.threshold = threshold;
    }

    public boolean thresholdIsMet(JmsApplicationEvent event) {
        if (!event.isSuccessful()) {
            addTimeStampToListAndMaintainWindowSize(event.getEventInstant());
        }
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

    private void addTimeStampToListAndMaintainWindowSize(Instant instant) {
        eventBuffer.add(instant);
        if (eventBuffer.size() > maxWindowSize) {
            eventBuffer.remove(0);
        }
    }
}
