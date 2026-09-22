package com.example.Kanban.Board.repository;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;

import java.util.List;

import com.example.Kanban.Board.mapper.TaskRowReducer;
import com.example.Kanban.Board.model.Task;


public interface NativeTaskRepository {
    
    @UseRowReducer(TaskRowReducer.class) 
    @SqlQuery("""
       WITH filtered_tasks as (SELECT * , ROW_NUMBER() OVER (PARTITION BY task_status ORDER BY task_order) AS int_row 
       FROM task WHERE (:id IS NULL OR id = :id) AND (:description IS NULL OR LOWER(description) like LOWER(CONCAT('%', :description, '%')) 
       OR LOWER(title) like LOWER(CONCAT('%', :description, '%')))) 
       SELECT t.id, t.int_row, t.task_priority, t.task_order,  t.task_status, t.version, t.description, t.title, 
       u.id as user_id, u.email as user_email, u.full_name as user_full_name, u.image as user_image FROM filtered_tasks t LEFT JOIN user_task ut on ut.task_id = t.id
       LEFT JOIN user u on u.id = ut.user_id WHERE t.int_row >= :start AND t.int_row < :end ORDER BY t.int_row
        """)
    List<Task> findTasks(
            @Bind("description") String description,
            @Bind("id") Long id,
            @Bind("start") int start,
            @Bind("end") int end
    );
}
