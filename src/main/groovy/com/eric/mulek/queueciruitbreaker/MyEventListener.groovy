package com.eric.mulek.queueciruitbreaker

import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Service
class MyEventListener {

    /*
    todo have pointcut target @JmsListener with an around that emited as
    success when completed with without exceptions, and an error when an exception occurs
    Also make it able to be disabled with a property to allow manual control of event emissions
     */
    @JmsListener(destination = '')
    @PointCut
    void process() {

    }
}

@Target([ElementType.METHOD, ElementType.ANNOTATION_TYPE])
@Retention(RetentionPolicy.RUNTIME)
@interface PointCut {}

@Slf4j
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(name = 'my.properties.jmsListener.pointCut.enabled', havingValue = 'true')
class ExamplePointCut {

    //todo not around, do AfterThrowing and AfterReturning
    @Around('@annotation(PointCut)')
    void logSomething(ProceedingJoinPoint joinPoint) {
        log.info('Before Proceed')
        try {
            joinPoint.proceed()
        } catch (Exception e) {
            log.info('caught', e)
        }
    }
}
