package com.jobhunter.api.auth.service;

import com.jobhunter.api.auth.exception.InvalidCredentialsException;
import com.jobhunter.api.user.entity.User;
import com.jobhunter.api.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public User authenticate(String email, String rawPassword) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(email, rawPassword);

        Authentication authenticated;

        try{
            authenticated = authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }

        return userRepository.findByEmail(authenticated.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user could not be found."));
    }
}
