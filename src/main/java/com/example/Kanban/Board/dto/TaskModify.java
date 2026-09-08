package com.example.Kanban.Board.dto;

import java.util.List;

import org.eclipse.microprofile.graphql.Input;

import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;

import jakarta.validation.constraints.NotNull;

@Input("TaskModifyInput")
public class TaskModify {

    private Long id;
  
    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private TaskStatus taskStatus;

    @NotNull
    private TaskPriority taskPriority;

    private String createdBy;

    private String updatedBy;

    private Integer taskOrder;

    private List<UserInput> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserInput> getUsers() {
        return users;
    }

    public void setUsers(List<UserInput> users) {
        this.users = users;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
