package com.eric.mulek.queueciruitbreaker.circuitbreaker;

public interface JmsCircuitBreakerPolicy {

    void process();

    void clear();
}
