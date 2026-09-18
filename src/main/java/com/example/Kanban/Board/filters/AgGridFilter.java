package com.example.Kanban.Board.filters;

public record AgGridFilter(
        String filterType,
        String type,
        Object filter,
        Object filterTo
) {
}
