package com.jobhunter.api.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String email,
        String firstName,
        String middleName,
        String lastName,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
