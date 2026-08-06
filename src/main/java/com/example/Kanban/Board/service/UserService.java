package com.example.Kanban.Board.service;

import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.UserConverter;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class UserService {

    private final UserConverter userConverter;

    private final UserRepository userRepository;

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
}
