package com.jobhunter.api.auth.service;

import com.jobhunter.api.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final Duration tokenExpiration;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${jobhunter.security.jwt.expiration}") Duration tokenExpiration) {
        this.jwtEncoder = jwtEncoder;
        this.tokenExpiration = tokenExpiration;
    }

    public String generateToken(User user){

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://api.jobhunter.local")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("authorities", List.of("ROLE_USER"))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public long expirationInSeconds() {
        return tokenExpiration.getSeconds();
    }
}
