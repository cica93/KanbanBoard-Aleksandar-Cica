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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenMethodException.class)
    public ResponseEntity<Object> handleForbiddenMethodException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotValidTaskPriorityException.class)
    public ResponseEntity<Object> handleNotValidTaskPriorityExceptionException(NotValidTaskPriorityException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<Object> handleUserDoesNotExistExceptionException(UserDoesNotExistException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidTaskStatusException.class)
    public ResponseEntity<Object> handleNotValidTaskStatusExceptionException(NotValidTaskStatusException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
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
