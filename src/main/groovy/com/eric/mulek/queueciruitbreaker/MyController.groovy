package com.eric.mulek.queueciruitbreaker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController {

    @Autowired
    MyTestRetryService myTestRetryService

    @GetMapping('hello')
    String hello() {
        myTestRetryService.getData('aTestValue')
    }
}
