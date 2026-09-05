package com.example.Kanban.Board.dto;

import java.util.List;

import org.openapitools.jackson.nullable.JsonNullable;

import com.example.Kanban.Board.model.TaskPriority;
import com.example.Kanban.Board.model.TaskStatus;
import com.example.Kanban.Board.model.User;

public class TaskPatchDTO {

    private Integer version;

    private JsonNullable<String> title = JsonNullable.undefined();

    private JsonNullable<String> description = JsonNullable.undefined();

    private JsonNullable<TaskStatus> taskStatus = JsonNullable.undefined();

    private JsonNullable<TaskPriority> taskPriority = JsonNullable.undefined();

    private JsonNullable<Integer> taskOrder = JsonNullable.undefined();

    private JsonNullable<List<User>> users = JsonNullable.undefined();

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public JsonNullable<String> getTitle() {
        return title;
    }

    public void setTitle(JsonNullable<String> title) {
        this.title = title;
    }

    public JsonNullable<String> getDescription() {
        return description;
    }

    public void setDescription(JsonNullable<String> description) {
        this.description = description;
    }

    public JsonNullable<TaskStatus> getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(JsonNullable<TaskStatus> taskStatus) {
        this.taskStatus = taskStatus;
    }

    public JsonNullable<TaskPriority> getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(JsonNullable<TaskPriority> taskPriority) {
        this.taskPriority = taskPriority;
    }

    public JsonNullable<Integer> getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(JsonNullable<Integer> taskOrder) {
        this.taskOrder = taskOrder;
    }

    public JsonNullable<List<User>> getUsers() {
        return users;
    }

    public void setUsers(JsonNullable<List<User>> users) {
        this.users = users;
    }
}
