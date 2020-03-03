package com.eric.mulek.queueciruitbreaker

import org.springframework.context.ApplicationEvent
import java.time.Instant

class JmsApplicationEvent extends ApplicationEvent {

    boolean success

    JmsApplicationEvent(Object source, boolean success) {
        super(source)
        this.success = success
    }

    Instant getEventInstant() {
        Instant.ofEpochMilli(this.getTimestamp())
    }
}