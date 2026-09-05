package com.example.Kanban.Board.controller;


import com.example.Kanban.Board.service.UserService;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.DefaultValue;
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
    public Response get(
            @QueryParam("order") @DefaultValue("desc") String order,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("column") @DefaultValue("id") String column,
            @QueryParam("limit") @DefaultValue("10") Integer limit,
            @QueryParam("keyword") String keyword) {

        Sort sort = "desc".equalsIgnoreCase(order)
                ? Sort.by(column).descending()
                : Sort.by(column).ascending();
        Page page = Page.of(offset / limit, limit);

        return userService.get(page, sort, keyword);
    }

    @GET
    @Path("/current")
    public Response currentUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String header) {
        return userService.currentUser(header);
    }

    @GET
    @Path("/has-mail/{email}")
    public Response hasMail(
            @PathParam("email") String email) {
        return userService.hasMail(email);
    }
}
