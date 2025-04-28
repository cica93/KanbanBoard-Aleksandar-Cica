package com.example.Kanban.Board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.TaskService;
import com.example.Kanban.Board.service.UserService;

@Controller
public class TaskGraphqlController {
    private TaskService taskService;
    private UserService userService;

    @Autowired
    public TaskGraphqlController(UserService userService, TaskService taskService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @QueryMapping
    public List<UserDTO> getUsers() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(1, 20, sort);
        return userService.get(pageRequest, "").getBody().getContent();
    }

    @MutationMapping
    public Object createTask(@ContextValue(name = "user") User user, @Argument("task") TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.create(user, taskDTO).getBody();
    }

    @MutationMapping
    public Object updateTask(@ContextValue(name = "user") User user, @Argument Long id, @Argument("task") TaskDTO task) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.update(user, id, task).getBody();
    }

    @MutationMapping
    public Object deleteTask(@ContextValue(name = "user") User user, @Argument Long id) {
        return taskService.delete(user, id).getBody();
    }

     @QueryMapping
     public TaskDTO getTaskById(@Argument Long id) {
         return taskService.getById(id).getBody();
     }

    @QueryMapping
    public List<TaskDTO> getTasks(@Argument Integer page, @Argument Integer pageSize,
                @Argument List<String> columns, @Argument String order, @Argument String description) {
                    Sort sort = Sort.by("desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, columns.stream().toArray(String[]::new));
                    PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
        return taskService.get(pageRequest, description).getBody();
    }


}
