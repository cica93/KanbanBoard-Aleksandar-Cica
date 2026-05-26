package com.example.Kanban.Board.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.Kanban.Board.daoHelper.TaskDTOResultSetExtractor;
import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;
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

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final Validator validator;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final TaskConverter taskConverter;
    
    private final UserConverter userConverter;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository,
            Validator validator,
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            TaskConverter taskConverter,
            UserConverter userConverter
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.validator = validator;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.taskConverter = taskConverter;
        this.userConverter = userConverter;
    }

    public ResponseEntity<List<TaskDTO>> get(Pageable pageable, String description) {
        String q = "WITH all_tasks as (SELECT *, ROW_NUMBER() OVER (PARTITION BY task_status ORDER BY task_order ) AS int_row "
                + "FROM task WHERE deleted = false AND (:description IS NULL OR LOWER(description) like LOWER(CONCAT('%', :description, '%')) "
                + "OR LOWER(title) like LOWER(CONCAT('%', :description, '%'))) order by task_order, id desc LIMIT :limit OFFSET :offset) "
                + "SELECT t.id, t.task_priority, t.task_order, t.task_status, t.created_by, t.updated_by, t.version, t.description, t.title, u.id as user_id, u.email, "
                + "u.full_name, u.image from all_tasks t LEFT JOIN user_task ut on ut.task_id = t.id "
                + "LEFT JOIN user u on u.id = ut.user_id WHERE t.int_row > :start AND t.int_row <= : end";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("description", description);
        params.addValue("end", pageable.getOffset() + pageable.getPageSize());
        params.addValue("start", pageable.getOffset());
        List<TaskDTO> result = namedParameterJdbcTemplate.query(q, params, new TaskDTOResultSetExtractor());
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<TaskDTO> getById(Long id) throws TaskDoesNotExistException {
        return taskRepository.findByIdAndDeletedFalse(id).map(task -> {
            return ResponseEntity.ok(taskConverter.convertModelToDTOModel(task));
        }).orElseThrow(() -> new TaskDoesNotExistException("Task not found"));
    }

    public ResponseEntity<?> create(User user, TaskDTO taskDTO) throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException  {
        Task task = taskConverter.convertDTOModelToModel(taskDTO);
        task.setCreatedBy(user.getEmail());
        return saveTask(task);
    }

    @Transactional
    public ResponseEntity<?> saveTask(Task task) throws UserDoesNotExistException {
        Map<String, String> errors = ModelValidator.validate(task, validator);
        if (task.getTaskOrder() == null) {
            task.setTaskOrder(0);
        }
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
            if (task.getId() == null) {
                taskRepository.increaseTaskOrder(- 1, task.getTaskStatus().ordinal());
            }
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

    public ResponseEntity<?> update(User user, Long id, TaskDTO taskDTO) throws NotValidTaskPriorityException,
            NotValidTaskStatusException, UserDoesNotExistException, TaskDoesNotExistException {
        return taskRepository.findByIdAndDeletedFalse(id).map(task -> {
            taskDTO.setId(id);
            taskDTO.setCreatedBy(task.getCreatedBy());
            taskDTO.setUpdatedBy(task.getUpdatedBy());
            taskDTO.setTaskOrder(task.getTaskOrder());
            try {
                return saveTask(taskConverter.convertDTOModelToModel(taskDTO));
            } catch (UserDoesNotExistException e) {
                return ResponseEntity.badRequest().body(Map.of("users", "One or more users provided do not exist"));
            }
        }).orElseThrow(() -> new TaskDoesNotExistException("Task not found"));
    }

    public ResponseEntity<?> dragTask(User user, DragTaskDTO dragTaskDTO) throws
            NotValidTaskStatusException, UserDoesNotExistException, TaskDoesNotExistException {
        return taskRepository.findByIdAndDeletedFalse(dragTaskDTO.getTaskId()).map(task -> {
            TaskStatus prevTaskStatus = task.getTaskStatus();
            TaskStatus taskStatus = this.taskConverter.convertStringToTaskStatus(dragTaskDTO.getTaskStatus());
            task.setTaskStatus(taskStatus);
            task.setTaskOrder(dragTaskDTO.getTaskOrder());
            try {
                saveTask(task);
                taskRepository.increaseTaskOrder(dragTaskDTO.getTaskOrder(), taskStatus.ordinal());
                taskRepository.decreaseTaskOrder(dragTaskDTO.getTaskOrder(), prevTaskStatus.ordinal());
                return ResponseEntity.ok().build();
            } catch (UserDoesNotExistException e) {
                return ResponseEntity.badRequest().body(Map.of("users", "One or more users provided do not exist"));
            }
        }).orElseThrow(() -> new TaskDoesNotExistException("Task not found"));
    }


    public ResponseEntity<?> patch(User user, Long id, TaskDTO taskDTO)
            throws NotValidTaskPriorityException, NotValidTaskStatusException, UserDoesNotExistException, TaskDoesNotExistException {
        return taskRepository.findByIdAndDeletedFalse(id).map(task -> {
            if (taskDTO.getDescription() != null) {
                task.setDescription(taskDTO.getDescription());
            }
            if (taskDTO.getTitle() != null) {
                task.setTitle(taskDTO.getTitle());
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
            try {
                return saveTask(task);
            } catch (UserDoesNotExistException e) {
                return ResponseEntity.badRequest().body(Map.of("users", "One or more users provided do not exist"));
            }
        }).orElseThrow(() -> new TaskDoesNotExistException("Task not found"));
    }

    @Transactional
    public ResponseEntity<?> delete(User user, Long id) throws TaskDoesNotExistException {
       return  taskRepository.findById(id).map(task ->{
           int result = taskRepository.deleteTask(id, user.getEmail());
           if (result == 1) {
               return ResponseEntity.ok().build();
           }
           taskRepository.deleteExistingUsersFromTask(id);
           return ResponseEntity.ok().build();
        }).orElseThrow(() -> new TaskDoesNotExistException("Task not found"));
    
    }
}
