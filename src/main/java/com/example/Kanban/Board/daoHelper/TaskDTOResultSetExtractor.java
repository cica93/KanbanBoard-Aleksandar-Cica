package com.example.Kanban.Board.daoHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.example.Kanban.Board.dto.TaskDTO;
import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;

public class TaskDTOResultSetExtractor extends ResultSetProcessor implements ResultSetExtractor<List<TaskDTO>> {

    @Override
    public List<TaskDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<TaskDTO> tasks = new ArrayList<>();
        while (rs.next()) {
            TaskDTO task = new TaskDTO();
            task.setUsers(new ArrayList<>());
            task.setId(rs.getLong("id"));
            task.setDescription(rs.getString("description"));
            task.setTitle(rs.getString("title"));
            task.setCreatedBy(rs.getString("created_by"));
            task.setUpdatedBy(rs.getString("updated_by"));
            task.setTaskPriority(TaskPriority.values()[rs.getInt("task_priority")].name());
            task.setTaskStatus(TaskStatus.values()[rs.getInt("task_status")].name());
            task.setVersion(rs.getInt("version"));
            Long id = getResultSetLong(rs, "user_id");
            if (id != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(id);
                userDTO.setEmail(rs.getString("email"));
                userDTO.setFullName(rs.getString("full_name"));
                userDTO.setImage(rs.getBytes("image"));
                task.getUsers().add(userDTO);
            }
            tasks.add(task);

        }

        return  tasks.stream()
                .collect(Collectors.groupingBy(
                        TaskDTO::getId,
                        LinkedHashMap::new,
                        Collectors.reducing((task1, task2) -> {
                            task1.getUsers().addAll(task2.getUsers());
                            return task1;
                        })
                )).values().stream().map(a -> a.get())
                .filter(v -> v != null)
                .collect(Collectors.toList());


    }
    
}
