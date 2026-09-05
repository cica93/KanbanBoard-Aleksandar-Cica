package com.example.Kanban.Board.controller;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.service.TaskService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.OptimisticLockException;

@GraphQLApi
@ApplicationScoped
public class TaskGraphqlController {

    private final TaskService taskService;

    public TaskGraphqlController(TaskService taskService) {
        this.taskService = taskService;
    }


    @Mutation("createTask")
    public Task createTask(@Name("task") TaskDTO taskDTO)
            throws UserDoesNotExistException {
        Task task = (Task) taskService.create("pera@gmail.com", taskDTO).getEntity();
        return task;
    }

    @Mutation("dragTask")
    public Task dragTask(@Name("taskId") Integer taskId, @Name("taskStatus") String taskStatus, @Name("taskOrder") Integer taskOrder, @Name("taskVersion") Integer taskVersion)
            throws NotValidTaskStatusException, TaskDoesNotExistException {
        DragTaskDTO dragTaskDTO = new DragTaskDTO();
        dragTaskDTO.setTaskId(taskId.longValue());
        dragTaskDTO.setTaskVersion(taskVersion);
        dragTaskDTO.setTaskStatus(taskStatus);
        dragTaskDTO.setTaskOrder(taskOrder);
        Task task = (Task) taskService.dragTask("pera@gmail.com", dragTaskDTO).getEntity();
        return task;
    }

    @Mutation("updateTask")
    public Task updateTask(@Name("id") Integer id, @Name("task") TaskDTO task) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException, TaskDoesNotExistException {
        return (Task) taskService.update("", id.longValue(), task).getEntity();
    }

    @Mutation("deleteTask")
    public TaskDTO deleteTask(@Name("id") Integer id, Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        return (TaskDTO) taskService.delete(id.longValue(), version).getEntity();
    }

    @Query("getTaskById")
    public Task getTaskById(@Name("id") Integer id) throws TaskDoesNotExistException {
        return (Task) taskService.getById(id.longValue()).getEntity();
     }

    @Query("getTasks")
    @SuppressWarnings("unchecked")
    public List<TaskDTO> getTasks(String description, @DefaultValue("20") Integer limit, @DefaultValue("0") Integer offset) throws SQLException {
        Object entity = taskService.get(limit, offset, description).getEntity();
        List<TaskDTO> l = (List<TaskDTO>) entity;
        return l;
    }


}
