package com.jobhunter.api.user.controller;

import com.jobhunter.api.AbstractIntegrationTest;
import com.jobhunter.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String CREATE_USER_URL = "/api/v1/users/createUser";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void whenUserIsCreated() throws Exception {

        String request = """
                {
                    "email": "api.test@example.com",
                    "password": "StrongPassword123!",
                    "firstName": "John",
                    "middleName": "Michael",
                    "lastName": "Doe",
                    "phoneNumber": "9876543210"
                }
                """;

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.email").value("api.test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.middleName").value("Michael"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        assertTrue(userRepository.existsByEmail("api.test@example.com"));

        var savedUser = userRepository.findByEmail("api.test@example.com")
                .orElseThrow();

        assertNotEquals("StrongPassword123!", savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(
                "StrongPassword123!",
                savedUser.getPasswordHash()
        ));
    }

    @Test
    void whenEmailAlreadyExists() throws Exception {

        String request = """
                {
                    "email": "duplicate.api@example.com",
                    "password": "StrongPassword123!",
                    "firstName": "Jane",
                    "lastName": "Doe"
                }
                """;

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void whenRequestIsInvalid() throws Exception {

        String request = """
                {
                    "email": "invalid-email",
                    "password": "StrongPassword123!",
                    "firstName": "",
                    "lastName": "Doe"
                }
                """;

        mockMvc.perform(post(CREATE_USER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.firstName").exists());
    }
}
