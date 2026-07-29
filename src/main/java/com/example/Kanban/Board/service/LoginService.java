package com.example.Kanban.Board.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.UserConverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class LoginService {

    private static final String SECRET =
        "my-super-secret-key-that-is-at-least-32-characters-long";

    private static final SecretKey SECRET_KEY =
        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${token.duration.in.hours}")
    private Integer durationInHours;

    public LoginService(UserConverter userConverter, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userConverter = userConverter;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public ResponseEntity<UserDTO> login(User user) throws BadCredentialsException {
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
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userConverter.convertModelToDTOModel(userFromDataBase));
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
