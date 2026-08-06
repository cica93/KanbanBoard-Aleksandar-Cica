package com.example.Kanban.Board.controller;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.example.Kanban.Board.exceptions.BadCredentialsException;
import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.exceptions.NotValidTaskPriorityException;
import com.example.Kanban.Board.exceptions.NotValidTaskStatusException;
import com.example.Kanban.Board.exceptions.TaskDoesNotExistException;
import com.example.Kanban.Board.exceptions.UserDoesNotExistException;


@Provider
public class GlobalExceptionHandler
        implements ExceptionMapper<Exception> {


    @Override
    public Response toResponse(Exception ex) {


        if (ex instanceof ForbiddenMethodException) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(new ErrorResponse(ex.getMessage()))
                    .build();
        }


        if (ex instanceof UserDoesNotExistException ||
            ex instanceof TaskDoesNotExistException ||
            ex instanceof NotValidTaskStatusException ||
            ex instanceof NotValidTaskPriorityException ||
            ex instanceof BadCredentialsException ||
            ex instanceof OptimisticLockException) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(ex.getMessage()))
                    .build();
        }


        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                    new ErrorResponse(
                        "An unexpected error occurred."
                    )
                )
                .build();
    }



    public static class ErrorResponse {

        private final String message;


        public ErrorResponse(String message) {
            this.message = message;
        }


        public String getMessage() {
            return message;
        }
    }
}