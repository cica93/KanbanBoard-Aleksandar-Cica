package com.example.Kanban.Board.dto;

import org.eclipse.microprofile.graphql.Input;

import com.example.Kanban.Board.model.TaskStatus;

import jakarta.validation.constraints.NotNull;

@Input("DragTaskInput")
public class DragTaskDTO {

    @NotNull
    private Long taskId;

    @NotNull
    private TaskStatus taskStatus;

    @NotNull
    private Integer taskOrder;

    @NotNull
    private Integer taskVersion;


    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public Integer getTaskVersion() {
        return taskVersion;
    }

    public void setTaskVersion(Integer taskVersion) {
        this.taskVersion = taskVersion;
    }
}
