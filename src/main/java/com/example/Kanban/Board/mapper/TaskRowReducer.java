package com.example.Kanban.Board.mapper;

import java.util.ArrayList;
import java.util.Map;

import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;

import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;


public class TaskRowReducer implements LinkedHashMapRowReducer<Long, Task> {

    @Override
    public void accumulate(Map<Long, Task> container, RowView row) {
        Long taskId = row.getColumn("id", Long.class);
        Task task = container.get(taskId);
        if (task == null) {
            task = new Task();
            task.setId(taskId);
            task.setTaskPriority(TaskPriority.values()[row.getColumn("task_priority", Integer.class)]);
            task.setTaskStatus(TaskStatus.values()[row.getColumn("task_status", Integer.class)]);
            task.setTaskOrder(row.getColumn("task_order", Integer.class));
            task.setVersion(row.getColumn("version", Integer.class));
            task.setTitle(row.getColumn("title", String.class));
            task.setDescription(row.getColumn("description", String.class));
            task.setUsers(new ArrayList<>());
        }
        User user = new User();
        user.setId(row.getColumn("user_id", Long.class));
        user.setEmail(row.getColumn("user_email", String.class));
        user.setFullName(row.getColumn("user_full_name", String.class));
        user.setImage(row.getColumn("user_image", byte[].class));
        task.getUsers().add(user);
        container.put(taskId, task);
      
    }
}
