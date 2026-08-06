package com.example.Kanban.Board.controller;


import com.example.Kanban.Board.annotations.CurrentUser;
import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.UserService;
import com.example.Kanban.Board.utilities.UserConverter;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

    private final UserConverter userConverter;

    @Inject
    @CurrentUser
    private User user;

    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
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
    public Response currentUser() {
        UserDTO dto
                = userConverter.convertModelToDTOModel(user);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/has-mail/{email}")
    public Response hasMail(
            @PathParam("email") String email) {
        return userService.hasMail(email);
    }
}
