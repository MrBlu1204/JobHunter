package com.jobhunter.api.auth.dto;

public record AuthenticationResponse(

        String tokenType,
        String accessToken,
        long expiresIn
) {
}
