package com.example.Kanban.Board.dto;

import java.util.List;

import com.example.Kanban.Board.model.Task;
import com.example.Kanban.Board.model.TaskStatus;

public class TasksByStatusDTO {

    private final TaskStatus status;
    private final List<Task> tasks;

    public TasksByStatusDTO(TaskStatus status, List<Task> tasks) {
        this.status = status;
        this.tasks = tasks;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
