package com.example.Kanban.Board.utilities;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtTokenUtil {

    public static final String DEFAULT_SECRET = "my-super-secret-key-that-is-at-least-32-characters-long";
    public static final String BEARER_PREFIX = "Bearer ";


    @ConfigProperty(name = "jwt.secret", defaultValue = DEFAULT_SECRET)
    String secret;

    public String createToken(String email) {
        return createToken(email, 2);
    }

    public String createToken(String email, int durationInHours) {
        Date now = new Date();
        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .claim("roles", Set.of("USER", "ADMIN"))
                .setExpiration(new Date(now.getTime() + durationInHours * 3600_000L))
                .signWith(key, SignatureAlgorithm.HS384)
                .compact();
    }

    public String extractSubject(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new SecurityException("Missing Authorization header");
        }

        String normalized = authorizationHeader.trim();
        if (!normalized.startsWith(BEARER_PREFIX)) {
            throw new SecurityException("Authorization header must use Bearer scheme");
        }

        String token = normalized.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new SecurityException("Bearer token is empty");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            throw new SecurityException("Invalid or expired JWT token "+ e.getMessage());
        }
    }

    public static String extractSubjectStatic(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new SecurityException("Missing Authorization header");
        }

        String normalized = authorizationHeader.trim();
        if (!normalized.startsWith(BEARER_PREFIX)) {
            throw new SecurityException("Authorization header must use Bearer scheme");
        }

        String token = normalized.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new SecurityException("Bearer token is empty");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(DEFAULT_SECRET))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            throw new SecurityException("Invalid or expired JWT token", e);
        }
    }

    private SecretKey getSigningKey() {
        return getSigningKey(secret == null || secret.isBlank() ? DEFAULT_SECRET : secret);
    }

    private static SecretKey getSigningKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }
}
