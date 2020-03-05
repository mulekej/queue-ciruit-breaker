package com.eric.mulek.queueciruitbreaker;

import org.springframework.context.event.EventListener;

public class BackOffStartManager {

    private ExponentialLockHandler lockHandler;
    private ConditionChecker conditionChecker;

    public BackOffStartManager(ExponentialLockHandler lockHandler, ConditionChecker conditionChecker) {
        this.lockHandler = lockHandler;
        this.conditionChecker = conditionChecker;
    }

    @EventListener
    public void eventListener(JmsApplicationEvent event) {
        if (conditionChecker.conditionIsMet(event.getEventInstant())) {
            lockHandler.process();
        } else {
            lockHandler.clear();
        }
    }
}
