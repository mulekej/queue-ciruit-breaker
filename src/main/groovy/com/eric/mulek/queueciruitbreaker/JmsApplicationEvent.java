package com.eric.mulek.queueciruitbreaker;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class JmsApplicationEvent extends ApplicationEvent {

    public JmsApplicationEvent(Object source) {
        super(source);
    }

    public Instant getEventInstant() {
        return Instant.ofEpochMilli(this.getTimestamp());
    }
}
