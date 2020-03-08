package com.eric.mulek.queueciruitbreaker;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class JmsApplicationEvent extends ApplicationEvent {

    public JmsApplicationEvent(Boolean success) {
        super(success);
    }

    public boolean isSuccessful() {
        return (Boolean) getSource();
    }

    public Instant getEventInstant() {
        return Instant.ofEpochMilli(this.getTimestamp());
    }
}
