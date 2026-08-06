package com.example.Kanban.Board.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;


import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.UserConverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import com.example.Kanban.Board.configuration.PasswordEncoder;

@ApplicationScoped
public class LoginService {

    private  final String SECRET =
        "my-super-secret-key-that-is-at-least-32-characters-long";

    private  final SecretKey SECRET_KEY =
        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserConverter userConverter, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userConverter = userConverter;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

 
    private  Integer durationInHours = 2;

    public Response login(User user) throws BadCredentialsException {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadCredentialsException("Password and email must be provided");
        }
        User userFromDataBase = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User with email "+user.getEmail()+" doesn't exist!"));
        
        boolean match = passwordEncoder.matches(user.getPassword(), userFromDataBase.getPassword());
        if (!match) {
            throw new BadCredentialsException("Wrong password!");
        }
        String token = createJWT(user.getEmail());
        userFromDataBase.setToken(token);
        int changed = userRepository.saveToken(userFromDataBase.getId(), token);
        if (changed == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("Token is not saved").build();
        }
        return Response.ok(userConverter.convertModelToDTOModel(userFromDataBase)).build();
    }


    private String createJWT(String email) {
    Date now = new Date();

    return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + durationInHours * 3600_000L))
            .signWith(SECRET_KEY)
            .compact();
    }


}
