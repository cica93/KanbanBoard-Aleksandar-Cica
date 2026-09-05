package com.example.Kanban.Board.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.Kanban.Board.utilities.JwtTokenUtil;

class JwtAuthValidationTest {

    @Test
    void validBearerTokenUsesTheConfiguredSecret() {
        String token = JwtTokenUtil.createTokenStatic("user@example.com", 2);

        assertEquals("user@example.com", JwtTokenUtil.extractSubjectStatic("Bearer " + token));
    }

    @Test
    void invalidTokenRejected() {
        assertThrows(SecurityException.class,
                () -> JwtTokenUtil.extractSubjectStatic("Bearer invalid.token.value"));
    }

    @Test
    void missingBearerPrefixRejected() {
        String token = JwtTokenUtil.createTokenStatic("user@example.com", 2);

        assertThrows(SecurityException.class,
                () -> JwtTokenUtil.extractSubjectStatic(token));
    }
}
