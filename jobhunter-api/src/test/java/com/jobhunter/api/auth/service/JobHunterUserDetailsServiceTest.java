package com.jobhunter.api.auth.service;

import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobHunterUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobHunterUserDetailsService userDetailsService;

    @Test
    void whenUserExists() {
        String email =  "john@example.com";
        String passwordHash = "$2a$12$encodedPassword";

        User user = mock(User.class);

        when(user.getEmail()).thenReturn(email);
        when(user.getPasswordHash()).thenReturn(passwordHash);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(email, userDetails.getUsername());
        assertEquals(passwordHash, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByEmail(email);
    }

    @Test
    void whenUserDoesNotExist() {
        String email = "unknown@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));

        verify(userRepository).findByEmail(email);
    }

}
