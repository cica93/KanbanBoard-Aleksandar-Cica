package com.example.Kanban.Board.exceptions;

public class ForbiddenMethodException extends RuntimeException {
    public ForbiddenMethodException(String message) {
        super(message);
    }
}