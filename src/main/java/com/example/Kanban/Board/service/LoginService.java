package com.example.Kanban.Board.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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


@Service
public class LoginService {

    private UserConverter userConverter;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Value("${token.duration.in.hours}")
    private Integer durationInHours;

    @Autowired
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
        Optional<User> optional = userRepository.findByEmail(user.getEmail());
        if (!optional.isPresent()) {
            throw new BadCredentialsException("User with email "+user.getEmail()+" doesn't exist!");
        }
        User userFromDataBase = optional.get();
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
                .setExpiration(new Date(now.getTime() + 3600 * 1000 * durationInHours))
                .compact();
    }


}
