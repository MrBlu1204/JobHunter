package com.jobhunter.api.user.repository;

import com.jobhunter.api.AbstractIntegrationTest;
import com.jobhunter.api.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenUserIsSaved() {
        User user = new User();
        user.setEmail("repository.test@example.com");
        user.setFirstName("John");
        user.setMiddleName("Michael");
        user.setLastName("Doe");
        user.setPhoneNumber("9876543210");

        User savedUser = userRepository.saveAndFlush(user);

        Optional<User> retrievedUser =
                userRepository.findByEmail("repository.test@example.com");

        assertTrue(retrievedUser.isPresent());
        assertNotNull(savedUser.getUserId());
        assertEquals("John", retrievedUser.get().getFirstName());
        assertEquals("Michael", retrievedUser.get().getMiddleName());
        assertEquals("Doe", retrievedUser.get().getLastName());
        assertEquals("9876543210", retrievedUser.get().getPhoneNumber());
    }

    @Test
    void whenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("duplicate.test@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");

        userRepository.saveAndFlush(user);

        assertTrue(userRepository.existsByEmail("duplicate.test@example.com"));
    }

}
