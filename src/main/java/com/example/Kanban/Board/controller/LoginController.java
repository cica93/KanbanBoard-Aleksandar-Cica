package com.example.Kanban.Board.controller;

import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.LoginService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginController {  

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @POST
    @Path("/login")
    public Response login(User user) throws BadCredentialsException {
        return loginService.login(user);
    }

}
