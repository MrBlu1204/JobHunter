package com.jobhunter.api.auth.service;

import com.jobhunter.api.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Jwt jwt;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtEncoder, Duration.ofMinutes(15));
    }

    @Test
    void whenTokenIsGenerated() {
        Long userId = 42L;
        String email = "john@example.com";
        String expectedToken = "test.jwt.token";

        User user = mock(User.class);

        when(user.getUserId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(email);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn(expectedToken);

        String token = jwtService.generateToken(user);

        assertEquals(expectedToken, token);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("https://api.jobhunter.local", claims.getClaim("iss").toString());
        assertEquals("42", claims.getSubject());
        assertEquals(email, claims.getClaimAsString("email"));
        assertEquals(List.of("ROLE_USER"),
                claims.getClaimAsStringList("authorities"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());

        assertEquals(Duration.ofMinutes(15),
                Duration.between(claims.getIssuedAt(), claims.getExpiresAt()));
    }

    @Test
    void whenExpirationSecondsAreRequested() {
        assertEquals(900, jwtService.expirationInSeconds());
    }
}
