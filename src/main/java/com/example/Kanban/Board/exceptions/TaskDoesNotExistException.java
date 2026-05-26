package com.example.Kanban.Board.exceptions;

public class TaskDoesNotExistException extends Exception {
    public TaskDoesNotExistException(String message) {
        super(message);
    }
}
