package com.example.Kanban.Board.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.UserConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

    private final UserConverter userConverter;
    private final UserRepository userRepository;

    private final String SECRET
            = "my-super-secret-key-that-is-at-least-32-characters-long";

    private final SecretKey SECRET_KEY
            = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public UserService(UserConverter userConverter, UserRepository userRepository) {
        this.userConverter = userConverter;
        this.userRepository = userRepository;   
    }

    public Response get(Page page, Sort sort, String keyword) {
        PanacheQuery<User> data;
        if (keyword != null && !keyword.isBlank()) {
            data = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(keyword, keyword, sort);
        } else {
            data = userRepository.findAll(sort);
        }
        data.page(page);
        return Response.ok(userConverter.convertListOfModelsToDTOModel(data.list())).build();
    }

    public Response hasMail(String email) {
        return Response.ok(userRepository.findByEmail(email).isPresent()).build();
    }

    public User findUser(String token) throws ForbiddenMethodException, Exception, UserDoesNotExistException {
        validateToken(token);
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new UserDoesNotExistException("User not found for token: " + token));

        return user;
    }

    private void validateToken(String token) throws Exception, ForbiddenMethodException {
        if (token == null || token.isBlank()) {
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
