package com.example.Kanban.Board.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jdbi.v3.core.Jdbi;

import com.example.Kanban.Board.mapper.TaskRowMapper;
import com.example.Kanban.Board.model.Task;

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
    private final Jdbi jdbi;

    public TaskRepository(EntityManager entityManager, TransactionManager transactionManager, Jdbi jdbi) {
        this.entityManager = entityManager;
        this.transactionManager = transactionManager;
        this.jdbi = jdbi;
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
        String sql = this.selectTaskQuery() + " FROM task t LEFT JOIN user_task ut ON ut.task_id = t.id "
                + "LEFT JOIN user u ON u.id = ut.user_id WHERE t.id = :id";
        return jdbi.withHandle(handle -> handle.createQuery(sql).bind("id", id)
                .registerRowMapper(new TaskRowMapper()).mapTo(Task.class).findFirst());

    }

    @Transactional
    public Task save(Task task) {
        if (task.getId() == null) {
            entityManager.persist(task);
            entityManager.flush();
            return task;
        }
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

    private String selectTaskQuery() {
        return "SELECT t.id, t.int_row, t.task_priority, t.task_order,  t.task_status, t.version, t.description, t.title, u.id as user_id, u.email as user_email, "
                + "u.full_name as user_full_name, u.image as user_image";
    }



    public List<Task> getTasks(Integer limit, Integer offset, String description) {
            String sql = "WITH all_tasks as (SELECT * , ROW_NUMBER() OVER (PARTITION BY task_status ORDER BY task_order) AS int_row "
                    + "FROM task WHERE (:description IS NULL OR LOWER(description) like LOWER(CONCAT('%', :description, '%')) "
                    + "OR LOWER(title) like LOWER(CONCAT('%', :description, '%')))) "
                    + this.selectTaskQuery() + " FROM all_tasks t LEFT JOIN user_task ut on ut.task_id = t.id "
                    + "LEFT JOIN user u on u.id = ut.user_id WHERE t.int_row >= :start AND t.int_row < :end ORDER BY t.int_row";


        return jdbi.withHandle(h -> h.createQuery(sql)
                    .bind("description", description)
                    .bind("start", offset + 1)
                    .bind("end", offset + limit + 1)
                    .registerRowMapper(new TaskRowMapper())
                    .mapTo(Task.class)
                .list()).stream()
                    .collect(Collectors.groupingBy(
                            Task::getId,
                            LinkedHashMap::new,
                        Collectors.reducing((acc, curr) -> {
                            acc.getUsers().addAll(curr.getUsers());
                            return acc;
                            })))
                    .values()
                    .stream()
                    .map(Optional::get)
                .collect(Collectors.toList());
    }
}
