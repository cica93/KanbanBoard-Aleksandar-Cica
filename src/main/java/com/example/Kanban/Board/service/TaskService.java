package com.example.Kanban.Board.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Kanban.Board.daoHelper.TaskDTOResultSetExtractor;
import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.TaskRepository;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.ModelValidator;
import com.example.Kanban.Board.utilities.TaskConverter;
import com.example.Kanban.Board.utilities.UserConverter;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    private UserRepository userRepository;

    private Validator validator;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private TaskConverter taskConverter;
    
    private UserConverter userConverter;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, Validator validator,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate, TaskConverter taskConverter, UserConverter userConverter) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.validator = validator;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.taskConverter = taskConverter;
        this.userConverter = userConverter;
    }

    public ResponseEntity<List<TaskDTO>> get(Pageable pageable, String description) {
        String q = "WITH all_tasks as (Select * FROM task WHERE deleted = false AND (:description IS NULL OR LOWER(description) like LOWER(CONCAT('%', :description, '%')) "
                +" OR LOWER(title) like LOWER(CONCAT('%', :description, '%'))) order by :orderBy LIMIT :limit OFFSET :offset) " +
                        "Select t.id, t.task_priority, t.task_status, t.created_by, t.updated_by, t.version, t.description, t.title, u.id as user_id, u.email, " +
                        "u.full_name, u.image from all_tasks t LEFT JOIN user_task ut on ut.task_id = t.id " + 
                "LEFT JOIN user u on u.id = ut.user_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        String orderBy = pageable.getSort().stream().map(order -> {
            return order.getProperty() +  (order.getDirection().equals(Sort.Direction.DESC) ? " desc": " asc");
        }).collect(Collectors.joining(", "));
        params.addValue("description", description);
        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());
        List<TaskDTO> result = namedParameterJdbcTemplate.query(q.replace(":orderBy", orderBy), params, new TaskDTOResultSetExtractor());
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<TaskDTO> getById(Long id) {
        return taskRepository.findByIdAndDeletedFalse(id).map(task -> {
            return ResponseEntity.ok(taskConverter.convertModelToDTOModel(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> create(User user, TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException  {
        Task task = taskConverter.convertDTOModelToModel(taskDTO);
        task.setCreatedBy(user.getEmail());
        return saveTask(task);
    }

    @Transactional
    public ResponseEntity<?> saveTask(Task task) throws UserDoesNotExistException {
        Map<String, String> errors = ModelValidator.validate(task, validator);
        Set<Long> userIds = null;
        if (errors == null) {
            if (task.getUsers() != null && !task.getUsers().isEmpty()) {
                userIds = task.getUsers().stream().map(User::getId).collect(Collectors.toSet()); // remove duplicates
                Long count = userRepository.countByIdIn(userIds);
                if (count != userIds.size()) {
                    throw new UserDoesNotExistException("Can not find all users provided in request body");
                }
            }
            task.setUsers(null);
            Task savedTask = taskRepository.save(task);
            if (task.getId() != null) {
                taskRepository.deleteExistingUsersFromTask(task.getId());
            }
         
            Long taskId = savedTask.getId();
            task.setId(taskId);
               if(userIds != null && !userIds.isEmpty()){
                userIds.forEach(userId -> {
                  taskRepository.assignUserToTask(taskId, userId);
                });
            }
            return ResponseEntity.ok(task);
        }
        return ResponseEntity.badRequest().body(errors);
    }

    public ResponseEntity<?> update(User user, Long id, TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        Optional<Task> optional = taskRepository.findByIdAndDeletedFalse(id);
        if (optional.isPresent()) {
            Task task = taskConverter.convertDTOModelToModel(taskDTO);
            task.setCreatedBy(optional.get().getCreatedBy());
            task.setUpdatedBy(user.getEmail());
            task.setId(id);
            task.setVersion(optional.get().getVersion());
            return saveTask(task);
        }
        return ResponseEntity.notFound().build();
    }


    public ResponseEntity<?> patch(User user, Long id, TaskDTO taskDTO)
            throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException {
        Optional<Task> optional = taskRepository.findByIdAndDeletedFalse(id);
        if (optional.isPresent()) {
            Task task = optional.get();
            if (taskDTO.getDescription() != null) {
                task.setDescription(task.getDescription());
            }
            if (taskDTO.getTitle() != null) {
                task.setTitle(task.getTitle());
            }
            if (taskDTO.getTaskStatus() != null) {
                task.setTaskStatus(taskConverter.convertStringToTaskStatus(taskDTO.getTaskStatus()));
            }
            if (taskDTO.getTaskPriority() != null) {
                task.setTaskPriority(taskConverter.convertStringToTaskPriority(taskDTO.getTaskPriority()));
            }
            if (taskDTO.getUsers() != null) {
                task.setUsers(userConverter.convertListOfDTOModelsTOModel(taskDTO.getUsers()));
            }
            task.setUpdatedBy(user.getEmail());
            return saveTask(task);
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    public ResponseEntity<?> delete(User user, Long id) {
       return  taskRepository.findById(id).map(task ->{
           int result = taskRepository.deleteTask(id, user.getEmail());
           if (result == 1) {
               return ResponseEntity.ok().build();
           }
           taskRepository.deleteExistingUsersFromTask(id);
           return ResponseEntity.ok().build();
       }).orElse(ResponseEntity.noContent().build());
    
    }
}
