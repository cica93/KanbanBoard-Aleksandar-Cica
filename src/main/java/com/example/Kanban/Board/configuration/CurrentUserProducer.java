package com.example.Kanban.Board.configuration;


import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.example.Kanban.Board.annotations.CurrentUser;
import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import jakarta.ws.rs.core.HttpHeaders;

import javax.crypto.SecretKey;


@RequestScoped
public class CurrentUserProducer {

    private final HttpHeaders httpHeaders;
    private final UserRepository userRepository;


    private  final String SECRET =
        "my-super-secret-key-that-is-at-least-32-characters-long";

    private  final SecretKey SECRET_KEY =
        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    
    public CurrentUserProducer(HttpHeaders httpHeaders, UserRepository userRepository) {
        this.httpHeaders = httpHeaders;
        this.userRepository = userRepository;
    }

    @Produces
    @CurrentUser
    public User getCurrentUser() throws UserDoesNotExistException, ForbiddenMethodException, Exception {
        if ("true".equals(httpHeaders.getHeaderString("skip-interceptor"))) {
            return null;
        }
        String token = httpHeaders.getHeaderString("token");
        if(token == null){
            return null;
        }
        return findUser(token);
    }


    private User findUser(String token) throws ForbiddenMethodException, Exception, UserDoesNotExistException {
        User user = userRepository.findByToken(token).orElseThrow(() -> new UserDoesNotExistException("User not found for token: " + token));
        validateToken(token);
        return user;
    }

    private void validateToken(String token) throws Exception, ForbiddenMethodException {
        if(token == null || token.isBlank()) {
            throw new Exception("token is not provided");
        }
        Date expirationDate = getExpirationDate(token);
        if (expirationDate.before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))) {
            throw new ForbiddenMethodException("Token has expired");
        }
    }

    private Date getExpirationDate(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }
}
