package com.example.Kanban.Board.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskPatchDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.TaskRepository;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.NotificationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Response get(Integer limit, Integer offset, String description) {
        List<Task> result = taskRepository.getTasks(limit, offset, description);
        return Response.ok(result).build();
    }

    public Response getById(Long id) throws TaskDoesNotExistException {
        Task task = taskRepository.findById(id);
        if (task == null) {
            throw new TaskDoesNotExistException("Task not found");
        }
        return Response.ok(task).build();
    }

    public Response create(String userEmail, Task task) throws UserDoesNotExistException {
        task.setCreatedBy(userEmail);
        return saveTask(userEmail, task);
    }

    @SuppressWarnings("null")
    @Transactional
    public Response saveTask(String userEmail, Task task) throws UserDoesNotExistException {
        if (task.getTaskOrder() == null) {
            task.setTaskOrder(0);
        }
        Set<Long> userIds = null;
            if (task.getUsers() != null && !task.getUsers().isEmpty()) {
                userIds = task.getUsers().stream().map(User::getId).collect(Collectors.toSet()); // remove duplicates
                Long count = userRepository.countByIdIn(userIds);
                if (count != userIds.size()) {
                    throw new UserDoesNotExistException("Can not find all users provided in request body");
                }
            }
            task.setUsers(null);
            Task savedTask = taskRepository.save(userEmail, task);
            if (savedTask.getId() != null) {
                task.setId(savedTask.getId());
            }
            if (task.getId() != null) {
                taskRepository.deleteLinksForTask(task.getId());
            }
            if (userIds != null && !userIds.isEmpty()) {
                userIds.forEach(userId -> taskRepository.assignUserToTask(task.getId(), userId));
            }

            savedTask.setUsers(null);
        return Response.ok(savedTask).build();

    }

    public Response update(String userEmail, Long id, Task task) throws UserDoesNotExistException, OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = taskRepository.findById(id);
        if (existingTask == null) {
            throw new TaskDoesNotExistException("Task not found");
        }

        Integer dtoVersion = task.getVersion();
        if (!existingTask.getVersion().equals(dtoVersion)) {
            throw new OptimisticLockException(
                        "Task already modified"
            );
        }
        task.setCreatedBy(existingTask.getCreatedBy());
        task.setUpdatedBy(userEmail);
        task.setTaskOrder(existingTask.getTaskOrder());
            try {
                return saveTask(userEmail, task);
            } catch (UserDoesNotExistException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("users", "One or more users provided do not exist"))
                        .build();
            }

    }

    public Response dragTask(String userEmail, DragTaskDTO dragTaskDTO) throws TaskDoesNotExistException {
        Task task = taskRepository.findById(dragTaskDTO.getTaskId());
        if (task == null) {
            throw new TaskDoesNotExistException("Task not found");
        }

        if (!task.getVersion().equals(dragTaskDTO.getTaskVersion())) {
            throw new OptimisticLockException(
                    "Task already modified");
        }

        TaskStatus prevTaskStatus = task.getTaskStatus();
        TaskStatus taskStatus = dragTaskDTO.getTaskStatus();
        task.setTaskStatus(taskStatus);
        task.setTaskOrder(dragTaskDTO.getTaskOrder());
        taskRepository.save(userEmail, task);
        taskRepository.updateTaskOrderForStatus(dragTaskDTO.getTaskOrder(), taskStatus.ordinal(), true);
        taskRepository.updateTaskOrderForStatus(task.getTaskOrder(), prevTaskStatus.ordinal(), false);
        task.setUsers(null);
        return Response.ok().entity(task).build();

    }


    public Response patch(String userEmail, Long id, TaskPatchDTO taskDTO)
            throws OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = taskRepository.findById(id);
        if (existingTask == null) {
            throw new TaskDoesNotExistException("Task not found");
        }
        if (!existingTask.getVersion().equals(taskDTO.getVersion())) {
                throw new OptimisticLockException(
                        "Task already modified"
                );
            }
        if (taskDTO.getDescription().isPresent()) {
            existingTask.setDescription(taskDTO.getDescription().get());
            }
        if (taskDTO.getTitle().isPresent()) {
            existingTask.setTitle(taskDTO.getTitle().get());
            }
        if (taskDTO.getTaskStatus().isPresent()) {
            existingTask.setTaskStatus(taskDTO.getTaskStatus().get());
            }
        if (taskDTO.getTaskPriority().isPresent()) {
            existingTask.setTaskPriority(taskDTO.getTaskPriority().get());
            }
        if (taskDTO.getUsers().isPresent()) {
            existingTask.setUsers(taskDTO.getUsers().get());
        }
            try {
                return saveTask(userEmail, existingTask);
            } catch (UserDoesNotExistException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("users", "One or more users provided do not exist"))
                    .build();
        }
    }

    @Transactional
    public Response delete(Long id, Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        Task task = taskRepository.findById(id);
        if (task == null) {
            throw new TaskDoesNotExistException("Task not found");
        }

        if (!task.getVersion().equals(version)) {
            throw new OptimisticLockException("Task already modified");
        }

        taskRepository.delete(task);

        return Response.ok().entity(1).build();
    }
}
