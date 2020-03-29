package com.eric.mulek.queueciruitbreaker;

import com.eric.mulek.queueciruitbreaker.circuitbreaker.MessagingCircuitBreakerPolicy;
import com.eric.mulek.queueciruitbreaker.circuitbreaker.MessagingCircuitBreakerThreshold;
import org.springframework.context.event.EventListener;

public class JmsListenerCircuitBreakerListener {

    private MessagingCircuitBreakerPolicy breakerPolicy;
    private MessagingCircuitBreakerThreshold breakerThreshold;

    public JmsListenerCircuitBreakerListener(MessagingCircuitBreakerPolicy breakerPolicy, MessagingCircuitBreakerThreshold breakerThreshold) {
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
