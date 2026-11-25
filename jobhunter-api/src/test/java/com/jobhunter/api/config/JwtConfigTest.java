package com.jobhunter.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    private static final String VALID_SECRET =
            Base64.getEncoder().encodeToString(
                    "01234567890123456789012345678901".getBytes()
            );

    private JwtConfig jwtConfig;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        secretKey = jwtConfig.jwtSecretKey(VALID_SECRET);
    }

    @Test
    void whenValidSecretIsProvided() {
        assertNotNull(secretKey);
        assertEquals("HmacSHA256", secretKey.getAlgorithm());
        assertEquals(32, secretKey.getEncoded().length);
    }

    @Test
    void whenSecretIsShorterThan32Bytes() {
        String shortSecret = Base64.getEncoder()
                .encodeToString("short-secret".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> jwtConfig.jwtSecretKey(shortSecret));
    }

    @Test
    void whenSecretIsNotValidBase64() {
        assertThrows(IllegalArgumentException.class,
                () -> jwtConfig.jwtSecretKey("invalid-base64"));
    }

    @Test
    void whenTokenHasValidSignatureAndIssuer() {
        JwtEncoder encoder = jwtConfig.jwtEncoder(secretKey);
        JwtDecoder decoder = jwtConfig.jwtDecoder(secretKey);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://api.jobhunter.local")
                .subject("42")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900))
                .build();

        String token = encoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();

        Jwt decodedJwt = decoder.decode(token);

        assertEquals("https://api.jobhunter.local", decodedJwt.getIssuer().toString());
        assertEquals("42", decodedJwt.getSubject());
    }

    @Test
    void whenTokenHasInvalidSignature() {
        JwtEncoder encoder = jwtConfig.jwtEncoder(secretKey);

        SecretKey differentSecret = jwtConfig.jwtSecretKey(
                Base64.getEncoder().encodeToString(
                        "98765432109876543210987654321098".getBytes()
                )
        );

        JwtDecoder decoder = jwtConfig.jwtDecoder(differentSecret);

        String token = generateToken(encoder, "https://api.jobhunter.local");

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    @Test
    void whenTokenHasInvalidIssuer() {
        JwtEncoder encoder = jwtConfig.jwtEncoder(secretKey);
        JwtDecoder decoder = jwtConfig.jwtDecoder(secretKey);

        String token = generateToken(encoder, "another-application");

        assertThrows(JwtException.class, () -> decoder.decode(token));
    }

    private String generateToken(JwtEncoder encoder, String issuer) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("42")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900))
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();
    }
}
