package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class KeyConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
