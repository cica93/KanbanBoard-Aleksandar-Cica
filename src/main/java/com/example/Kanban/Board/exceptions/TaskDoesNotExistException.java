package com.example.Kanban.Board.exceptions;

public class TaskDoesNotExistException extends Exception {
    public TaskDoesNotExistException(Long id) {
        super("Task with id " + id + " doesn't exist");
    }
}
