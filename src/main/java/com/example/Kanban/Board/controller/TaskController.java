package com.example.Kanban.Board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private TaskService taskService;
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @GetMapping
    public ResponseEntity<List<TaskDTO>> get(User user, Pageable pageable,
            @RequestParam(name = "description", required = false) String description) {
        return taskService.get(pageable, description);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(User user, @PathVariable Long id) {
        return taskService.getById(id);
    }
    
    @PostMapping()
    public ResponseEntity<?> create(User user, @RequestBody TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.create(user, taskDTO);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> create(User user, @PathVariable Long id, @RequestBody TaskDTO taskDTO)
            throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.update(user, id, taskDTO);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(User user, @PathVariable Long id, @RequestBody TaskDTO taskDTO)
            throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        return taskService.patch(user, id, taskDTO);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(User user, @PathVariable Long id)  {
        return taskService.delete(user, id);
    }

}
