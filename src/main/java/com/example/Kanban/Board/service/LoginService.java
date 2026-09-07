package com.example.Kanban.Board.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;

import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.JwtTokenUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class LoginService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final Integer durationInHours;

    public LoginService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, @ConfigProperty(name = "token.duration.in.hours") Integer durationInHours) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.durationInHours = durationInHours;
    }



    public Response login(User user) throws BadCredentialsException {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadCredentialsException("Password and email must be provided");
        }
        User userFromDataBase = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User with email "+user.getEmail()+" doesn't exist!"));
        boolean match = BCrypt.checkpw(user.getPassword(), userFromDataBase.getPassword());
        if (!match) {
            throw new BadCredentialsException("Wrong password!");
        }
        String token = jwtTokenUtil.createToken(user.getEmail(), durationInHours);
        userFromDataBase.setToken(token);
        int changed = userRepository.saveToken(userFromDataBase.getId(), token);
        if (changed == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Token is not saved").build();
        }
        return Response.ok(userFromDataBase).build();
    }


}
