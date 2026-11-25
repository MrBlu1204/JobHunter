package com.jobhunter.api.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("/api/v1/test/protected")
    public String protectedEndpoint() {
        return "Authenticated successfully";
    }
}
