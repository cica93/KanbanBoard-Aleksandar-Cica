package com.example.Kanban.Board.service;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdbi.v3.core.Jdbi;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.dto.TaskPatchDTO;
import com.example.Kanban.Board.dto.TasksByStatusDTO;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.repository.NativeTaskRepository;
import com.example.Kanban.Board.repository.TaskRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import jakarta.transaction.TransactionManager;

import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class TaskService {

    private final TaskRepository taskRepository;
    private final EntityManager entityManager;
    private final TransactionManager transactionManager;
    private final Jdbi jdbi;

    public TaskService(TaskRepository taskRepository, EntityManager entityManager, TransactionManager transactionManager, Jdbi jdbi) {
        this.taskRepository = taskRepository;
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
        this.jdbi = jdbi;
    }

    public Response get(Integer limit, Integer offset, String description) {
        List<Task> tasks = jdbi.withExtension(NativeTaskRepository.class, repo -> repo.findTasks(description, null, offset + 1, offset + limit + 1));

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
    public Response create(Task task) {
        task.setTaskOrder(0);
        return saveTask(task);
    }

    @Transactional
    public Response saveTask(Task task) {
        Task savedTask = save(task);
        task.setId(savedTask.getId());
        return Response.ok(task).build();
    }

    @Transactional
    public Response saveTask(String userEmail, Task task) {
        task.setCreatedBy(userEmail);
        return saveTask(task);
    }

    @Transactional
    public Response update(Long id, Integer version, Task task) throws OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = findByIdOrElseThrow(id);
        checkVersions(existingTask.getVersion(), version);
        task.setCreatedBy(existingTask.getCreatedBy());
        task.setTaskOrder(existingTask.getTaskOrder());
        task.setVersion(version);
        task.setId(id);
        return saveTask(task);
    }

    @Transactional
    public Response dragTask(DragTaskDTO dragTaskDTO) throws TaskDoesNotExistException {
        Task task = findByIdIcluedeUsers(dragTaskDTO.getTaskId());
        checkVersions(task.getVersion(), dragTaskDTO.getTaskVersion());
        TaskStatus taskStatus = dragTaskDTO.getTaskStatus();
        task.setTaskStatus(taskStatus);
        task.setTaskOrder(dragTaskDTO.getTaskOrder());
        save(task);
        updateTaskOrderForStatus(dragTaskDTO.getTaskOrder(), taskStatus.ordinal(), true);
        updateTaskOrderForStatus(task.getTaskOrder(), task.getTaskStatus().ordinal(), false);
        return Response.ok().entity(task).build();
    }


    @Transactional
    public Response patch(Long id, TaskPatchDTO taskPatchDTO)
            throws OptimisticLockException, TaskDoesNotExistException {
        Task existingTask = findByIdIcluedeUsers(id);
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
        return saveTask(existingTask);
    }

    @Transactional
    public Response delete(Long id, Integer version) throws TaskDoesNotExistException, OptimisticLockException {
        Task task = findByIdOrElseThrow(id);
        checkVersions(task.getVersion(), version);
        try {
            taskRepository.deleteById(task.getId());
            updateTaskOrderForStatus(task.getTaskOrder(), task.getTaskStatus().ordinal(), false);
        } catch (Exception e) {
            try {
                transactionManager.setRollbackOnly();
            } catch (SystemException | IllegalStateException ignored) {
            }

            throw new RuntimeException(e);
        }
        return Response.ok().entity(Map.of("deleted", 1)).build();
    }

    public void updateTaskOrderForStatus(Integer taskOrder, int taskStatus, boolean increase) {
        if (taskOrder == null) {
            return;
        }
        String sql = increase
                ? "UPDATE task SET task_order = task_order + 1 WHERE task_order >= :taskOrder AND task_status = :taskStatus"
                : "UPDATE task SET task_order = GREATEST(task_order - 1, 0) WHERE task_order > :taskOrder AND task_status = :taskStatus";

        entityManager.createNativeQuery(sql)
                .setParameter("taskOrder", taskOrder)
                .setParameter("taskStatus", taskStatus)
                .executeUpdate();
    }

    private Task findByIdOrElseThrow(Long id) throws TaskDoesNotExistException {
        return taskRepository.findByIdOptional(id).orElseThrow(() -> new TaskDoesNotExistException(id));
    }

    private void checkVersions(Integer databaseVersion, Integer taskVersion) throws OptimisticLockException {
        if (!databaseVersion.equals(taskVersion)) {
            throw new OptimisticLockException("Task already modified");
        }
    }

    private Task findByIdIcluedeUsers(Long id) throws TaskDoesNotExistException {
        return jdbi.withExtension(NativeTaskRepository.class, repo -> repo.findTasks(null, id, 1, 2))
                .stream().findFirst()
                .orElseThrow(() -> new TaskDoesNotExistException(id));

    }

    public Task save(Task task) {
        if (task.getId() == null) {
            taskRepository.persist(task);
            taskRepository.flush();
            return task;
        }
        Task merged = entityManager.merge(task);
        entityManager.flush();
        return merged;
    }

}
