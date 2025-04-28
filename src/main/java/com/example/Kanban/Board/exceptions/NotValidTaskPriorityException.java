package com.example.Kanban.Board.exceptions;

public class NotValidTaskPriorityException extends RuntimeException {
    public NotValidTaskPriorityException(String message) {
        super(message);
    }
}
