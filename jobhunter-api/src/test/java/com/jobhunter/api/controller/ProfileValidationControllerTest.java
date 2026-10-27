package com.jobhunter.api.controller;

import com.jobhunter.api.exception.GlobalExceptionHandler;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileValidationController.class)
@Import(GlobalExceptionHandler.class)
class ProfileValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/api/v1/profiles/validate";

    @Test
    void shouldReturn200WhenRequestIsValid() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Manish",
                            "email":    "manish@example.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Profile request is valid."));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "name": "   ",
                                  "email": "manish@example.com"
                                }
                          """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.path").value(ENDPOINT))
                .andExpect(jsonPath("$.validationErrors.name").value("Name is required"));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Manish",
                                  "email": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("Email must be a valid email address"));
    }

    @Test
    void shouldReturn400WhenEmailIsMissing() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Manish"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("Email is required"));
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Manish",
                                  "email":
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Request body is missing or contains invalid JSON"))
                .andExpect(jsonPath("$.validationErrors").isEmpty());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value(ENDPOINT))
                .andExpect(jsonPath("$.message")
                        .value("Request body is missing or contains invalid JSON"));
    }

    @Test
    void shouldReturn400WhenNameExceedsMaximumLength() throws Exception {
        String longName = "a".repeat(101);

        String requestBody = """
                {
                  "name": "%s",
                  "email": "manish@example.com"
                }
                """.formatted(longName);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Name must not exceed 100 characters"));
    }

    @Test
    void shouldReturn400WhenEmailExceedsMaximumLength() throws Exception {
        // 255 characters, with an email-like format.
        String longEmail = "a".repeat(64)
                + "@"
                + "b".repeat(63)
                + "."
                + "c".repeat(63)
                + "."
                + "d".repeat(63);

        org.junit.jupiter.api.Assertions.assertEquals(
                256, longEmail.length());

        String requestBody = """
                {
                  "name": "Manish",
                  "email": "%s"
                }
                """.formatted(longEmail);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("Email must not exceed 254 characters"));
    }

    @Test
    void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": "Manish",
                              "email": "manish@example.com"
                            }
                            """))
                .andExpect(status().isOk());
    }
}
