package com.example.Kanban.Board.exceptions;

public class NotValidTaskStatusException extends RuntimeException {
public NotValidTaskStatusException(String message) {
        super(message);
    }
}
