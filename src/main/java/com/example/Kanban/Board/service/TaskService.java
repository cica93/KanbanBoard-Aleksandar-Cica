package com.example.Kanban.Board.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskPatchDTO;
import com.example.Kanban.Board.dto.TasksByStatusDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.repository.TaskRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Response get(Integer limit, Integer offset, String description) {
        List<TasksByStatusDTO> result = taskRepository.getTasks(limit, offset, description).stream()
                .collect(Collectors.groupingBy(Task::getTaskStatus, LinkedHashMap::new,
                        Collectors.toList())).entrySet()
                .stream()
                .map(entry -> new TasksByStatusDTO(
                entry.getKey(),
                entry.getValue()
        ))
                .toList();

        return Response.ok(result).build();
    }

    public Response getById(Long id) throws TaskDoesNotExistException {
        Task task = taskRepository.findById(id);
        if (task == null) {
            throw new TaskDoesNotExistException("Task not found");
        }
        return Response.ok(task).build();
    }

    public Response create(String userEmail, Task task) {
        task.setCreatedBy(userEmail);
        return saveTask(userEmail, task);
    }

    @Transactional
    public Response saveTask(String userEmail, Task task) {
        Task savedTask = taskRepository.save(userEmail, task);
        task.setId(savedTask.getId());
        return Response.ok(task).build();
    }

    @Transactional
    public Response update(String userEmail, Long id, Task task) throws OptimisticLockException, TaskDoesNotExistException {
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
        return saveTask(userEmail, task);
    }

    @Transactional
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
        return saveTask(userEmail, existingTask);
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

        return Response.ok().entity(Map.of("deleted", 1)).build();
    }
}
