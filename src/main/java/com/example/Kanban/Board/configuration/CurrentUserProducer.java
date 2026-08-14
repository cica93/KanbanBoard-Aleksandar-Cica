package com.example.Kanban.Board.configuration;


import com.example.Kanban.Board.annotations.CurrentUser;
import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.UserService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.core.HttpHeaders;


@RequestScoped
public class CurrentUserProducer {

    private final HttpHeaders httpHeaders;
    private final UserService userService;

    public CurrentUserProducer(HttpHeaders httpHeaders, UserService userService) {
        this.httpHeaders = httpHeaders;
        this.userService = userService;
    }

    @Produces
    @CurrentUser
    public User getCurrentUser() throws UserDoesNotExistException, ForbiddenMethodException, Exception {
        if ("true".equals(httpHeaders.getHeaderString("skip-interceptor"))) {
            return null;
        }
        String token = httpHeaders.getHeaderString("token");
        if(token == null){
            return null;
        }
        return userService.findUser(token);
    }
}
