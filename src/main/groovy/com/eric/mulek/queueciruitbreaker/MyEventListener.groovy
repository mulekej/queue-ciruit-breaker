package com.eric.mulek.queueciruitbreaker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.config.JmsListenerEndpointRegistry
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.locks.ReentrantLock

@Service
@EnableJms
class MyEventListener {

    @JmsListener(destination = '')
    void listner1() {

    }
}

@Component
class BackOffStartManager {

    @Autowired
    ExponentialLockHandler lockHandler
    @Autowired
    ConditionChecker conditionChecker

    @EventListener
    void eventListener(JmsApplicationEvent event) {
        conditionChecker.conditionIsMet(event.eventInstant) ? lockHandler.process() : lockHandler.clear()
    }
}

@Component
class ConditionChecker {

    List<Instant> eventBuffer = []
    int maxWindowSize = 3
    int minWindowSize = 2
    long threshold = 3000

    boolean conditionIsMet(Instant currentEventTimeStamp) {
        addTimeStampToListAndMaintainWindowSize(currentEventTimeStamp)
        eventBuffer.size() == minWindowSize ? isThresholdSurpassed() : false
    }

    private boolean isThresholdSurpassed() {
        List temp = []
        for (int i = 0; i < eventBuffer.size() - 1; i++) {
            temp.add(ChronoUnit.MILLIS.between(eventBuffer[i + 1], eventBuffer[i]))
        }
        return (temp.sum() / temp.size()) < threshold
    }

    void addTimeStampToListAndMaintainWindowSize(Instant instant) {
        eventBuffer.add(instant)
        if (eventBuffer.size() > maxWindowSize) {
            eventBuffer.remove(0)
        }
    }
}

