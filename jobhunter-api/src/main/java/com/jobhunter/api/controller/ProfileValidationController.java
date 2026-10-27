package com.jobhunter.api.controller;

import com.jobhunter.api.dto.ProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileValidationController {

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validate(
            @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(
                Map.of("message", "Profile request is valid.")
        );
    }
}
