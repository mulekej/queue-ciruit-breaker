package com.eric.mulek.queueciruitbreaker.circuitbreaker;

import com.eric.mulek.queueciruitbreaker.MessagingCircuitBreakerEvent;

public interface MessagingCircuitBreakerThreshold {

    boolean thresholdIsMet(MessagingCircuitBreakerEvent event);
}
