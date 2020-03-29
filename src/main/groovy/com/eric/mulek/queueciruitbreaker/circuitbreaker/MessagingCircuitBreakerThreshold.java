package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.JmsApplicationEvent;

public interface MessagingCircuitBreakerThreshold {

    boolean thresholdIsMet(JmsApplicationEvent event);
}
