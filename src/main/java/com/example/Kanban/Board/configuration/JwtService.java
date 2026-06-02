package com.example.Kanban.Board.configuration;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public Authentication parseToken(String token) {

        // validate JWT here
        String username = extractUsername(token);

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of()
        );
    }

    private String extractUsername(String token) {
        return "alex";
    }
}

