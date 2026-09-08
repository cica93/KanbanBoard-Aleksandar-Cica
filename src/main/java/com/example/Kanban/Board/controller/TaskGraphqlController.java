package com.example.Kanban.Board.controller;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.example.Kanban.Board.configuration.JsonWebToken;
import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskModify;
import com.example.Kanban.Board.dto.TasksByStatusDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.constraints.NotNull;

@GraphQLApi
@ApplicationScoped
public class TaskGraphqlController {

    private final TaskService taskService;
    private final JsonWebToken jwt;

    @Inject
    public TaskGraphqlController(TaskService taskService, JsonWebToken jwt) {
        this.taskService = taskService;
        this.jwt = jwt;
    }


    @Mutation("createTask")
    public Task createTask(@NotNull @Name("task") TaskModify taskInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        Task created = (Task) taskService.create(jwt.getSubject(), objectMapper.convertValue(taskInput, Task.class)).getEntity();
        return created;
    }

    @Mutation("dragTask")
    public Task dragTask(@NotNull @Name("dragTask") DragTaskDTO dragTaskDTO)
            throws TaskDoesNotExistException {
        Task task = (Task) taskService.dragTask(jwt.getSubject(), dragTaskDTO).getEntity();
        return task;
    }

    @Mutation("updateTask")
    public Task updateTask(@NotNull @Name("id") Integer id, @NotNull @Name("version") Integer version,
            @NotNull @Name("task") TaskModify taskInput) throws TaskDoesNotExistException {
        ObjectMapper objectMapper = new ObjectMapper();
        return (Task) taskService.update(jwt.getSubject(), id.longValue(), version, objectMapper.convertValue(taskInput, Task.class)).getEntity();
    }

    @Mutation("deleteTask")
    public Task deleteTask(@NotNull @Name("id") Integer id, @NotNull @Name("version") Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        return (Task) taskService.delete(id.longValue(), version).getEntity();
    }

    @Query("getTaskById")
    public Task getTaskById(@NotNull @Name("id") Integer id) throws TaskDoesNotExistException {
        return (Task) taskService.getById(id.longValue()).getEntity();
     }

    @Query("getTasks")
    @SuppressWarnings("unchecked")
    public List<TasksByStatusDTO> getTasks(String description, @DefaultValue("20") Integer limit, @DefaultValue("0") Integer offset) throws SQLException {
        return (List<TasksByStatusDTO>) taskService
                .get(limit, offset, description).getEntity();
    }

}
