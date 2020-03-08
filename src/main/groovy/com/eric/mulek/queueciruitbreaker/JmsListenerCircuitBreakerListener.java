package com.eric.mulek.queueciruitbreaker;

import com.eric.mulek.queueciruitbreaker.circuitbreaker.AverageTimeBetweenErrorThreshold;
import com.eric.mulek.queueciruitbreaker.circuitbreaker.ExponentialBackOffCircuitBreakerPolicy;
import com.eric.mulek.queueciruitbreaker.circuitbreaker.JmsCircuitBreakerPolicy;
import com.eric.mulek.queueciruitbreaker.circuitbreaker.JmsCircuitBreakerThreshold;
import org.springframework.context.event.EventListener;

public class JmsListenerCircuitBreakerListener {

    private JmsCircuitBreakerPolicy breakerPolicy;
    private JmsCircuitBreakerThreshold breakerThreshold;

    public JmsListenerCircuitBreakerListener(ExponentialBackOffCircuitBreakerPolicy breakerPolicy, AverageTimeBetweenErrorThreshold breakerThreshold) {
        this.breakerPolicy = breakerPolicy;
        this.breakerThreshold = breakerThreshold;
    }

    @EventListener
    public void eventListener(JmsApplicationEvent event) {
        if (breakerThreshold.thresholdIsMet(event)) {
            breakerPolicy.process();
        } else {
            breakerPolicy.clear();
        }
    }
}
