package com.jobhunter.api.controller;

import com.jobhunter.api.dto.ApiStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello")
    public ApiStatusResponse hello(){
        return new ApiStatusResponse("Welcome to the Job Hunt", "UP");
    }
}
