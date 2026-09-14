package com.example.Kanban.Board.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;

public class TaskRowMapper implements RowMapper<Task> {

    @Override
    public Task map(ResultSet rs, StatementContext ctx)
            throws SQLException {

        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTaskPriority(TaskPriority.values()[rs.getInt("task_priority")]);
        task.setTaskStatus(TaskStatus.values()[rs.getInt("task_status")]);
        task.setTaskOrder(rs.getInt("task_order"));
        task.setVersion(rs.getObject("version", Integer.class));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("user_email"));
        user.setFullName(rs.getString("user_full_name"));
        user.setImage(rs.getBytes("user_image"));
        List<User> list = new ArrayList<>();
        list.add(user);
        task.setUsers(list);
        return task;
    }
}
