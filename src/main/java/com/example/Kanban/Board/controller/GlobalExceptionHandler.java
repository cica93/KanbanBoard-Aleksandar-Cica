package com.example.Kanban.Board.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ForbiddenMethodException.class)
    public ResponseEntity<Object> handleForbiddenMethodException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(exception = {
        UserDoesNotExistException.class,
        NotValidTaskStatusException.class,
        NotValidTaskPriorityException.class,
        BadCredentialsException.class
    })
    public ResponseEntity<Object> handleUserDoesNotExistExceptionException(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse( "An unexpected error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    static class ErrorResponse {

        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
