package com.jobhunter.api.user.service;

import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.exception.EmailAlreadyExistsException;
import com.jobhunter.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void whenEmailDoesNotExist() {

        // Arrange
        String email = "john@example.com";
        String rawPassword = "StrongPassword123!";
        String encodedPassword = "$2a$12$encodedPassword";

        User user = mock(User.class);

        when(user.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        // Act
        User savedUser = userService.createUser(user, rawPassword);

        // Assert
        assertEquals(user, savedUser);

        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(rawPassword);
        verify(user).setPasswordHash(encodedPassword);
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void whenEmailAlreadyExists() {

        // Arrange
        String email = "john@example.com";
        User user = mock(User.class);

        when(user.getEmail()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(user, "StrongPassword123!")
        );

        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

}
