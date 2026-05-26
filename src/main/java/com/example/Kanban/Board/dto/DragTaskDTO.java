package com.example.Kanban.Board.dto;


public class DragTaskDTO {
    private Long taskId;
    private String taskStatus;
    private Integer taskOrder;

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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
}
