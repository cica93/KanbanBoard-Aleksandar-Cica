package com.example.Kanban.Board.service;

import com.example.Kanban.Board.configuration.PasswordEncoder;
import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.JwtTokenUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class LoginService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

 
    private  Integer durationInHours = 2;

    public Response login(User user) throws BadCredentialsException {
        if (user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadCredentialsException("Password and email must be provided");
        }
        UserDTO userFromDataBase = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new BadCredentialsException("User with email "+user.getEmail()+" doesn't exist!"));
        
        // boolean match = passwordEncoder.matches(user.getPassword(), userFromDataBase.getPassword());
        // if (!match) {
        //     throw new BadCredentialsException("Wrong password!");
        // }
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
