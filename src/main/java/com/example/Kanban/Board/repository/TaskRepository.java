package com.example.Kanban.Board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Kanban.Board.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    @Modifying
    @Transactional
    @Query(value ="DELETE FROM user_task WHERE task_id = ?1", nativeQuery=true)
    int deleteExistingUsersFromTask(Long taskId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Task t SET task_order = GREATEST(task_order - 1, 0) WHERE task_order >= ?1 AND task_status = ?2", nativeQuery = true)
    int decreaseTaskOrder(Integer taskOrder, int taskStatus);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Task t SET task_order = task_order + 1 WHERE task_order >= ?1 AND task_status = ?2", nativeQuery = true)
    int increaseTaskOrder(Integer taskOrder, int taskStatus);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_task (task_id, user_id) values (?1, ?2)", nativeQuery = true)
    void assignUserToTask(Long taskId, Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Task t WHERE t.id = ?1 AND t.version = ?2", nativeQuery = true)
    int deleteByIdAndVersion(Long id, Integer version);
}
