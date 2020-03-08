package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent;

public interface JmsCircuitBreakerThreshold {

    boolean thresholdIsMet(JmsApplicationEvent event);
}
