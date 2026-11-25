package com.jobhunter.api.auth.controller;

import com.jobhunter.api.auth.dto.AuthenticationResponse;
import com.jobhunter.api.auth.dto.LoginRequest;
import com.jobhunter.api.auth.service.AuthenticationService;
import com.jobhunter.api.auth.service.JwtService;
import com.jobhunter.api.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authenticationService.authenticate(loginRequest.email(), loginRequest.password());

        String accessToken = jwtService.generateToken(user);

        return new AuthenticationResponse(
                "Bearer",
                accessToken,
                jwtService.expirationInSeconds()
        );
    }
}
