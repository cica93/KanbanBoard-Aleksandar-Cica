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
import com.example.Kanban.Board.dto.TasksByStatusDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.service.TaskService;
import com.example.Kanban.Board.utilities.EnumUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.OptimisticLockException;

@GraphQLApi
@ApplicationScoped
public class TaskGraphqlController {

    private final TaskService taskService;
    private final JsonWebToken jwt;

    public TaskGraphqlController(TaskService taskService, JsonWebToken jwt) {
        this.taskService = taskService;
        this.jwt = jwt;
    }


    @Mutation("createTask")
    public Task createTask(@Name("task") Task task)
            throws UserDoesNotExistException {
        Task created = (Task) taskService.create(jwt.getSubject(), task).getEntity();
        return created;
    }

    @Mutation("dragTask")
    public Task dragTask(@Name("taskId") Integer taskId, @Name("taskStatus") String taskStatus, @Name("taskOrder") Integer taskOrder, @Name("taskVersion") Integer taskVersion)
            throws TaskDoesNotExistException {
        DragTaskDTO dragTaskDTO = new DragTaskDTO();
        dragTaskDTO.setTaskId(taskId.longValue());
        dragTaskDTO.setTaskVersion(taskVersion);
        dragTaskDTO.setTaskStatus(EnumUtils.valueOf(TaskStatus.class, taskStatus));
        dragTaskDTO.setTaskOrder(taskOrder);
        Task task = (Task) taskService.dragTask(jwt.getSubject(), dragTaskDTO).getEntity();
        return task;
    }

    @Mutation("updateTask")
    public Task updateTask(@Name("id") Integer id, @Name("task") Task task) throws UserDoesNotExistException, TaskDoesNotExistException {
        return (Task) taskService.update(jwt.getSubject(), id.longValue(), task).getEntity();
    }

    @Mutation("deleteTask")
    public Task deleteTask(@Name("id") Integer id, Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        return (Task) taskService.delete(id.longValue(), version).getEntity();
    }

    @Query("getTaskById")
    public Task getTaskById(@Name("id") Integer id) throws TaskDoesNotExistException {
        return (Task) taskService.getById(id.longValue()).getEntity();
     }

    @Query("getTasks")
    @SuppressWarnings("unchecked")
    public List<TasksByStatusDTO> getTasks(String description, @DefaultValue("20") Integer limit, @DefaultValue("0") Integer offset) throws SQLException {
        return (List<TasksByStatusDTO>) taskService
                .get(limit, offset, description).getEntity();
    }

}
