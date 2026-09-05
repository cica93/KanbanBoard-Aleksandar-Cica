package com.example.Kanban.Board.service;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.JwtTokenUtil;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
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

    public Response get(Page page, Sort sort, String keyword) {
        PanacheQuery<UserDTO> data
                = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase("", keyword, sort);

        data.page(page);
        return Response.ok(data.list()).build();
    }

    public Response hasMail(String email) {
        return Response.ok(userRepository.findByEmail(email).isPresent()).build();
    }

    public Response currentUser(String header) {
        String subject = jwtTokenUtil.extractSubject(header);
        return Response.ok(userRepository.findByEmail(subject)).build();
    }
}
