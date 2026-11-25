package com.jobhunter.api.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(@Value("${jobhunter.security.jwt.secret}") String encodedSecret){

        byte[] secretBytes = Base64.getDecoder().decode(encodedSecret);

        if(secretBytes.length < 32){
            throw new IllegalArgumentException("JWT signing secret must contain at least 32 bytes");
        }

        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey){
        return new NimbusJwtEncoder(
                new ImmutableSecret<SecurityContext>(jwtSecretKey)
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey){
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer("https://api.jobhunter.local")
        );

        return decoder;
    }
}
