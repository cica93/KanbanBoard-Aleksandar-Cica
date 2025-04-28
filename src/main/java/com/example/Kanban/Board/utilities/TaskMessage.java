package com.example.Kanban.Board.utilities;

import com.example.Kanban.Board.model.Task;

public class TaskMessage {

    private Object task;
    private String message;

    public TaskMessage(Object object) {
        this.task = object;
        if (object instanceof Task task) {
            if (task.isDeleted()) {
                message = "Task is deleted";
            } else if (task.getUpdatedBy() == null) {
                message = "Task is updated";
            } else {
                message = "Task is created";
            }
        }
    }

    public Object getTask() {
        return task;
    }

    public String getMessage() {
        return message;
    }

}
    
