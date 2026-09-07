package com.example.Kanban.Board.service;

import java.util.List;

import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.JwtTokenUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Response get(String keyword) {
        List<User> data
                = userRepository.findByFullNameContainingIgnoreCase(keyword);
        return Response.ok(data).build();
    }

    public Response hasMail(String email) {
        return Response.ok(userRepository.findByEmail(email).isPresent()).build();
    }

    public Response currentUser(String header) {
        String subject = jwtTokenUtil.extractSubject(header);
        return Response.ok(userRepository.findByEmail(subject)).build();
    }
}
