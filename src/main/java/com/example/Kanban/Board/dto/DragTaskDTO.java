package com.example.Kanban.Board.dto;

import com.example.Kanban.Board.model.TaskStatus;

public class DragTaskDTO {
    private Long taskId;
    private String taskStatus;
    private Integer taskOrder;
    private Integer taskVersion;
    private String prevTaskStatus;
    private TaskStatus taskStatusAsEnumStatus;

    public TaskStatus getTaskStatusAsEnumStatus() {
        return taskStatusAsEnumStatus;
    }

    public void setTaskStatusAsEnumStatus(TaskStatus taskStatusAsEnumStatus) {
        this.taskStatusAsEnumStatus = taskStatusAsEnumStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getPrevTaskStatus() {
        return prevTaskStatus;
    }

    public void setPrevTaskStatus(String prevTaskStatus) {
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
