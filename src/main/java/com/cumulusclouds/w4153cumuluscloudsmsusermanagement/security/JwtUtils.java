package com.cumulusclouds.w4153cumuluscloudsmsusermanagement.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    private final SecretKey secretKey;

    public JwtUtils(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRoleFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // return claims.get("role", String.class);
        return "MUSICIAN";
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
            logger.info("Token validated successfully: " + claims.getSubject());
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String generateJwtToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                // .claim("roles", userDetails.getAuthorities().stream()
                //         .map(GrantedAuthority::getAuthority)
                //         .collect(Collectors.toList()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
