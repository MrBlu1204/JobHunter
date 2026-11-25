package com.jobhunter.api.config;


import com.jobhunter.api.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;


@Component
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public SecurityErrorHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        writeErrorResponse(
                request,
                response,
                HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource"
        );
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException {

        writeErrorResponse(
                request,
                response,
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource"
        );
    }

    private void writeErrorResponse( HttpServletRequest request, HttpServletResponse response, HttpStatus status, String message ) throws IOException {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                Map.of()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        jsonMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
