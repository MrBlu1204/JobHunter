package com.jobhunter.api.auth.service;


import com.jobhunter.api.auth.exception.InvalidCredentialsException;
import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void whenCredentialsAreValid(){
        String email = "john@example.com";
        String rawPassword = "StrongPassword123!";

        User user = mock(User.class);
        Authentication authenticated = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class)))
        .thenReturn(authenticated);
        when(authenticated.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = authenticationService.authenticate(email, rawPassword);

        assertSame(user, result);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager).authenticate(captor.capture());

        Authentication authenticationRequest = captor.getValue();

        assertEquals(email, authenticationRequest.getPrincipal());
        assertEquals(rawPassword, authenticationRequest.getCredentials());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void whenCredentialsAreInvalid(){
        String email = "john@example.com";
        String rawPassword = "WrongPassword123!";

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(email, rawPassword));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verifyNoInteractions(userRepository);


    }

    @Test
    void whenAuthenticatedUserCannotBeFound(){
        String email = "john@example.com";
        String rawPassword = "StrongPassword123!";

        Authentication authenticated = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authenticated);
        when(authenticated.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> authenticationService.authenticate(email, rawPassword));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(userRepository).findByEmail(email);

    }
}
