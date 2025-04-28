package com.example.Kanban.Board.repository;

import java.util.Optional;

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
    @Query(value = "INSERT INTO user_task (task_id, user_id) values (?1, ?2)", nativeQuery = true)
    void assignUserToTask(Long taskId, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.deleted = true, t.updatedBy = ?2 WHERE t.id = ?1")
    int deleteTask(Long id, String updatedBy);

    Optional<Task> findByIdAndDeletedFalse(Long id);
}
