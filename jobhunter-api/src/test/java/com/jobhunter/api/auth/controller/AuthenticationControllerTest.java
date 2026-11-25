package com.jobhunter.api.auth.controller;

import com.jobhunter.api.auth.exception.InvalidCredentialsException;
import com.jobhunter.api.auth.service.AuthenticationService;
import com.jobhunter.api.auth.service.JwtService;
import com.jobhunter.api.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void whenCredentialsAreValid() throws Exception {
        String email = "john@example.com";
        String password = "StrongPassword123!";
        String accessToken = "test.jwt.token";

        User user = mock(User.class);

        when(authenticationService.authenticate(email, password))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(accessToken);
        when(jwtService.expirationInSeconds()).thenReturn(900L);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john@example.com",
                                    "password": "StrongPassword123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.expiresIn").value(900));

        verify(authenticationService).authenticate(email, password);
        verify(jwtService).generateToken(user);
        verify(jwtService).expirationInSeconds();
    }

    @Test
    void whenCredentialsAreInvalid() throws Exception {
        String email = "john@example.com";
        String password = "WrongPassword123!";

        when(authenticationService.authenticate(email, password))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john@example.com",
                                    "password": "WrongPassword123!"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verify(authenticationService).authenticate(email, password);
        verifyNoInteractions(jwtService);
    }

    @Test
    void whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "invalid-email",
                                    "password": "StrongPassword123!"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService, jwtService);
    }

    @Test
    void whenCredentialsAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "",
                                    "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService, jwtService);
    }

}
