package com.example.Kanban.Board.controller;


import com.example.Kanban.Board.service.UserService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GET
    public Response get(@QueryParam("keyword") String keyword) {
        return userService.get(keyword);
    }

    @GET
    @Path("/current")
    public Response currentUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        return userService.currentUser(header);
    }

    @GET
    @Path("/has-email/{email}")
    public Response hasMail(
            @PathParam("email") String email) {
        return userService.hasMail(email);
    }
}
