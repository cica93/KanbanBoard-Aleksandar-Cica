package com.example.Kanban.Board.controller;


import com.example.Kanban.Board.configuration.JsonWebToken;
import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskPatchDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.service.TaskService;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskController {

    private final TaskService taskService;
    private final JsonWebToken jwt;

    public TaskController(TaskService taskService, JsonWebToken jwt) {
        this.taskService = taskService;
        this.jwt = jwt;
    }


    @GET
    public Response get(
            @QueryParam("description") String description,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("limit") @DefaultValue("10") Integer limit) {
        return taskService.get(limit, offset, description); 
    }

    @GET
    @Path("/{id}")
    public Response getById(
            @PathParam("id") Long id)
            throws TaskDoesNotExistException {
        return taskService.getById(id);
    }

    @POST
    public Response create(@Valid Task task) {
        return taskService.create(
                jwt.getSubject(),
                task
        );
    }

    @PUT
    @Path("/{id}/{version}")
    public Response update(
            @PathParam("id") Long id,
            @PathParam("version") Integer version,
            @Valid Task task)
            throws TaskDoesNotExistException {


        return taskService.update(
                jwt.getSubject(),
                id,
                version,
                task
        );
    }



    @PUT
    @Path("/drag")
    public Response dragTask(
            DragTaskDTO dragTaskDTO)
            throws TaskDoesNotExistException {
        return taskService.dragTask(
                jwt.getSubject(),
                dragTaskDTO
        );
    }



    @PATCH
    @Path("/{id}")
    public Response patch(
            @PathParam("id") Long id,
            TaskPatchDTO taskDTO)
            throws TaskDoesNotExistException {


        return taskService.patch(
                jwt.getSubject(),
                id,
                taskDTO
        );
    }



    @DELETE
    @Path("/{id}/{version}")
    public Response delete(
            @PathParam("id") Long id,
            @PathParam("version") Integer version)
            throws TaskDoesNotExistException,
                   OptimisticLockException {


        return taskService.delete(
                id,
                version
        );
    }
}