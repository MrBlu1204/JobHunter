package com.jobhunter.api.auth.controller;

import com.jobhunter.api.AbstractIntegrationTest;
import com.jobhunter.api.auth.service.JwtService;
import com.jobhunter.api.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(SecurityTestController.class)
class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void whenPublicHelloEndpointIsAccessedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void whenRegistrationIsAccessedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/users/createUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenProtectedEndpointIsAccessedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/test/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenProtectedEndpointIsAccessedWithMalformedToken() throws Exception {
        mockMvc.perform(get("/api/v1/test/protected")
                        .header("Authorization", "Bearer not-a-valid-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenProtectedEndpointIsAccessedWithInvalidSignature() throws Exception {
        String validToken = generateValidToken();

        String[] tokenParts = validToken.split("\\.");
        String invalidSignature = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(new byte[32]);

        String invalidToken = tokenParts[0] + "."
                + tokenParts[1] + "."
                + invalidSignature;

        mockMvc.perform(get("/api/v1/test/protected")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenProtectedEndpointIsAccessedWithValidToken() throws Exception {
        String token = generateValidToken();

        mockMvc.perform(get("/api/v1/test/protected")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated successfully"));
    }

    private String generateValidToken() {
        User user = mock(User.class);

        when(user.getUserId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("john@example.com");

        return jwtService.generateToken(user);
    }
}
