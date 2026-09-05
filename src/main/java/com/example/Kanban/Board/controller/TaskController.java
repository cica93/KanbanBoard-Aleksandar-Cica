package com.example.Kanban.Board.controller;

import java.sql.SQLException;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.service.TaskService;

import jakarta.persistence.OptimisticLockException;
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


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GET
    public Response get(
            @QueryParam("description") String description,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("limit") @DefaultValue("10") Integer limit) throws SQLException {
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
    public Response create(TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.create(
                null,
                taskDTO
        );
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @PathParam("id") Long id,
            TaskDTO taskDTO)
            throws NotValidTaskPriorityException,
                   NotValidTaskStatusException,
                   UserDoesNotExistException,
                   TaskDoesNotExistException {


        return taskService.update(
                currentUser,
                id,
                taskDTO
        );
    }



    @PUT
    @Path("/drag")
    public Response dragTask(
            DragTaskDTO dragTaskDTO)
            throws NotValidTaskStatusException,TaskDoesNotExistException {
        return taskService.dragTask(
                currentUser,
                dragTaskDTO
        );
    }



    @PATCH
    @Path("/{id}")
    public Response patch(
            @PathParam("id") Long id,
            TaskDTO taskDTO)
            throws NotValidTaskPriorityException,
                   NotValidTaskStatusException,
                   UserDoesNotExistException,
                   TaskDoesNotExistException {


        return taskService.patch(
                currentUser,
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