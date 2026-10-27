package com.jobhunter.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestExceptionController {

    @PostMapping("/test/exception")
    public void throwUnexpectedException() {
        throw new IllegalStateException("Sensitive internal details");
    }
}
