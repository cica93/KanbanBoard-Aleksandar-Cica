package com.example.Kanban.Board.dto;

import com.example.Kanban.Board.model.TaskStatus;

public class DragTaskDTO {
    private Long taskId;
    private TaskStatus taskStatus;
    private Integer taskOrder;
    private Integer taskVersion;
    private TaskStatus prevTaskStatus;


    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public TaskStatus getPrevTaskStatus() {
        return prevTaskStatus;
    }

    public void setPrevTaskStatus(TaskStatus prevTaskStatus) {
        this.prevTaskStatus = prevTaskStatus;
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
