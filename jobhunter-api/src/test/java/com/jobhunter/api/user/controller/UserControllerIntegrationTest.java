package com.jobhunter.api.user.controller;

import com.jobhunter.api.AbstractIntegrationTest;
import com.jobhunter.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String CREATE_USER_URL = "/api/v1/users/createUser";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenUserIsCreated() throws Exception {

        String request = """
                {
                    "email": "api.test@example.com",
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
    }

    @Test
    void whenEmailAlreadyExists() throws Exception {

        String request = """
                {
                    "email": "duplicate.api@example.com",
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
