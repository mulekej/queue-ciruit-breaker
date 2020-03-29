package com.eric.mulek.queueciruitbreaker.circuitbreaker;

public interface MessagingCircuitBreakerPolicy {

    void process();

    void clear();
}
