package com.example.Kanban.Board.service;

import java.util.List;

import com.example.Kanban.Board.dto.PageResponse;
import com.example.Kanban.Board.filters.Filter;
import com.example.Kanban.Board.filters.Operator;
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

    public Response get(String fullName, Integer limit, Integer offset, String columnSort, String direction) {
        PageResponse<User> data
                = userRepository.findByFilters(List.of(fullName == null ? null : new Filter("fullName", Operator.CONTAINS, fullName)), limit, offset, columnSort, direction, true);
        return Response.ok(data).build();
    }

    public Response hasMail(String email) {
        boolean isPresent = userRepository.findByEmail(email).isPresent();
        return Response.ok(isPresent).build();
    }

    public Response currentUser(String header) {
        User user = userRepository.findByEmail(jwtTokenUtil.extractSubject(header)).get();
        return Response.ok(user).build();
    }
}
