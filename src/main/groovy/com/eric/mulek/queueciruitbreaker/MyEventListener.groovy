package com.eric.mulek.queueciruitbreaker

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.util.ErrorHandler

@Service
class MyEventListener {

    /*
    todo have pointcut target @JmsListener with an around that emited as
    success when completed with without exceptions, and an error when an exception occurs
    Also make it able to be disabled with a property to allow manual control of event emissions
     */
//    @JmsListener(destination = '')
    void listener1() {

    }
}


@Slf4j
@Service
class MyErrorHandler implements ErrorHandler {

    @Override
    void handleError(Throwable t) {
        handle(t)
    }

    void handle(MyCustomException ex) {
        log.error('special exception', ex)
    }

    void handle(Throwable t) {
        log.debug('General exception', t)
    }
}

class MyCustomException extends Exception {
    Object finalStatus
}
