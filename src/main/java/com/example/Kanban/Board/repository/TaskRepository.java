package com.example.Kanban.Board.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.query.NativeQuery;

import com.example.Kanban.Board.dto.DragTaskDTO;
import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    private final EntityManager entityManager;
    private final TransactionManager transactionManager;

    public TaskRepository(EntityManager entityManager, TransactionManager transactionManager) {
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
    }

    @Transactional
    @Override
    public void delete(Task task) {
        try {
            deleteById(task.getId());
            updateTaskOrderForStatus(task.getTaskOrder(), task.getTaskStatus().ordinal(), false);
        } catch (Exception e) {
            try {
                transactionManager.setRollbackOnly();
            } catch (SystemException | IllegalStateException ignored) {
            }

            throw new RuntimeException(e);
        }

    }

    public Optional<Task> findByIdIncludingUsers(Long id) {
        String sql = "SELECT t.id, t.task_priority, t.task_order, t.task_status, t.created_by, t.updated_by, t.version, t.description, t.title, u.id as user_id, u.email, "
                + "u.full_name, u.image from task t LEFT JOIN user_task ut on ut.task_id = t.id "
                + "LEFT JOIN user u on u.id = ut.user_id WHERE t.id = :id";

        NativeQuery<Task> query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);

        query.setParameter("id", id);

        return this.extractUser(query).stream().findFirst();
    }

    @Transactional
    public Task save(String userEmail, Task task) {
        if (task.getId() == null) {
            task.setCreatedBy(userEmail);
            entityManager.persist(task);
            entityManager.flush();
            return task;
        }
        task.setUpdatedBy(userEmail);
        Task merged = entityManager.merge(task);
        entityManager.flush();
        return merged;
    }


    @Transactional
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

    @Transactional
    public void updateTaskStatus(DragTaskDTO dragTaskDTO, User user) {
        if (dragTaskDTO.getTaskOrder() == null) {
            return;
        }
        String sql = "UPDATE task SET task_order = :taskOrder, task_status = :taskStatus, updated_by = :updatedBy WHERE id = :taskId AND version = :version";

        entityManager.createNativeQuery(sql)
                .setParameter("taskOrder", dragTaskDTO.getTaskOrder())
                .setParameter("taskStatus", dragTaskDTO.getTaskStatus().ordinal())
                .setParameter("updatedBy", user.getEmail())
                .setParameter("taskId", dragTaskDTO.getTaskId())
                .setParameter("version", dragTaskDTO.getTaskVersion())
                .executeUpdate();
    }

    private List<Task> extractUser(NativeQuery<Task> query) {
        query.setTupleTransformer((tuple, aliases) -> {
            Task dto = new Task();
            dto.setId(((Number) tuple[0]).longValue());
            dto.setTaskPriority(TaskPriority.values()[((Number) tuple[1]).intValue()]);
            dto.setTaskOrder(((Number) tuple[2]).intValue());
            dto.setTaskStatus(TaskStatus.values()[((Number) tuple[3]).intValue()]);
            dto.setCreatedBy((String) tuple[4]);
            dto.setUpdatedBy((String) tuple[5]);
            dto.setVersion(((Number) tuple[6]).intValue());
            dto.setDescription((String) tuple[7]);
            dto.setTitle((String) tuple[8]);
            Long userId = tuple[9] != null ? ((Number) tuple[9]).longValue() : null;
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                user.setEmail((String) tuple[10]);
                user.setFullName((String) tuple[11]);
                user.setImage((byte[]) tuple[12]);
                dto.setUsers(new ArrayList<>());
                dto.getUsers().add(user);
            }
            return dto;
        });

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Task> getTasks(Integer limit, Integer offset, String description) {
        String sql = "WITH all_tasks as (SELECT * , ROW_NUMBER() OVER (PARTITION BY task_status ORDER BY task_order) AS int_row "
                + "FROM task WHERE (:description IS NULL OR LOWER(description) like LOWER(CONCAT('%', :description, '%')) "
                + "OR LOWER(title) like LOWER(CONCAT('%', :description, '%')))) "
                + "SELECT t.id, t.task_priority, t.task_order, t.task_status, t.created_by, t.updated_by, t.version, t.description, t.title, u.id as user_id, u.email, "
                + "u.full_name, u.image from all_tasks t LEFT JOIN user_task ut on ut.task_id = t.id "
                + "LEFT JOIN user u on u.id = ut.user_id WHERE t.int_row >= :start AND t.int_row < :end ORDER BY t.int_row";
        NativeQuery<Task> query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);

        query.setParameter("description", description);
        query.setParameter("start", offset + 1);
        query.setParameter("end", offset + limit + 1);

        List<Task> resultList = this.extractUser(query).stream()
                .collect(Collectors.groupingBy(
                        Task::getId,
                        LinkedHashMap::new,
                        Collectors.reducing((task1, task2) -> {
                            task1.getUsers().addAll(task2.getUsers());
                            return task1;
                        })
                )).values().stream().map(a -> a.get())
                .filter(v -> v != null)
                .collect(Collectors.toList());
        return resultList;

    }
}
