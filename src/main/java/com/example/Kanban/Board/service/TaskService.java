package com.example.Kanban.Board.service;


import java.util.Arrays;
import java.util.Collections;
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

        List<Task> tasks = taskRepository.getTasks(limit, offset, description);

        Map<TaskStatus, List<Task>> tasksByStatusMap = tasks.stream().collect(Collectors.groupingBy(Task::getTaskStatus, Collectors.toList()));

        List<TasksByStatusDTO> result = Arrays.stream(TaskStatus.values())
                .map(status -> new TasksByStatusDTO(
                status,
                tasksByStatusMap.getOrDefault(status, Collections.emptyList())
        )).toList();

        return Response.ok(result).build();
    }

    public Response getById(Long id) throws TaskDoesNotExistException {
        Task task = findByIdOrElseThrow(id);
        return Response.ok(task).build();
    }

    @Transactional
    public Response create(String userEmail, Task task) {
        task.setCreatedBy(userEmail);
        task.setTaskOrder(0);
        return saveTask(userEmail, task);
    }

    @Transactional
    public Response saveTask(String userEmail, Task task) {
        Task savedTask = taskRepository.save(userEmail, task);
        task.setId(savedTask.getId());
        return Response.ok(task).build();
    }

    @Transactional
    public Response update(String userEmail, Long id, Integer version, Task task) throws OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = findByIdOrElseThrow(id);
        checkVersions(existingTask.getVersion(), version);
        task.setCreatedBy(existingTask.getCreatedBy());
        task.setUpdatedBy(userEmail);
        task.setTaskOrder(existingTask.getTaskOrder());
        task.setVersion(version);
        task.setId(id);
        return saveTask(userEmail, task);
    }

    @Transactional
    public Response dragTask(String userEmail, DragTaskDTO dragTaskDTO) throws TaskDoesNotExistException {
        Task task = taskRepository.findByIdIncludingUsers(dragTaskDTO.getTaskId())
                .orElseThrow(() -> new TaskDoesNotExistException(dragTaskDTO.getTaskId()));

        checkVersions(task.getVersion(), dragTaskDTO.getTaskVersion());
        TaskStatus taskStatus = dragTaskDTO.getTaskStatus();
        task.setTaskStatus(taskStatus);
        task.setTaskOrder(dragTaskDTO.getTaskOrder());
        taskRepository.save(userEmail, task);
        taskRepository.updateTaskOrderForStatus(dragTaskDTO.getTaskOrder(), taskStatus.ordinal(), true);
        taskRepository.updateTaskOrderForStatus(task.getTaskOrder(), task.getTaskStatus().ordinal(), false);
        return Response.ok().entity(task).build();

    }


    @Transactional
    public Response patch(String userEmail, Long id, TaskPatchDTO taskPatchDTO)
            throws OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = taskRepository.findByIdIncludingUsers(id).orElseThrow(() -> new TaskDoesNotExistException(id));
        checkVersions(existingTask.getVersion(), taskPatchDTO.getVersion());
        if (taskPatchDTO.getDescription().isPresent()) {
            existingTask.setDescription(taskPatchDTO.getDescription().get());
        }

        if (taskPatchDTO.getTitle().isPresent()) {
            existingTask.setTitle(taskPatchDTO.getTitle().get());
        }

        if (taskPatchDTO.getTaskStatus().isPresent()) {
            existingTask.setTaskStatus(taskPatchDTO.getTaskStatus().get());
        }

        if (taskPatchDTO.getTaskPriority().isPresent()) {
            existingTask.setTaskPriority(taskPatchDTO.getTaskPriority().get());
        }

        if (taskPatchDTO.getUsers().isPresent()) {
            existingTask.setUsers(taskPatchDTO.getUsers().get());
        }
        return saveTask(userEmail, existingTask);
    }

    @Transactional
    public Response delete(Long id, Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        Task task = findByIdOrElseThrow(id);
        checkVersions(task.getVersion(), version);
        taskRepository.delete(task);
        return Response.ok().entity(Map.of("deleted", 1)).build();
    }

    private Task findByIdOrElseThrow(Long id) throws TaskDoesNotExistException {
        Task task = taskRepository.findById(id);
        if (task == null) {
            throw new TaskDoesNotExistException(id);
        }
        return task;
    }

    private void checkVersions(Integer databaseVersion, Integer taskVersion) throws OptimisticLockException {
        if (!databaseVersion.equals(taskVersion)) {
            throw new OptimisticLockException("Task already modified");
        }
    }
}
