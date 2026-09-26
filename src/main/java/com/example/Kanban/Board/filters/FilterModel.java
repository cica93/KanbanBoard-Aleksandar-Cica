package com.example.Kanban.Board.filters;

import java.util.List;

public record FilterModel(
        String filterType,
        String operator,
        List<FilterModel> conditions,
        String type,
        Object filter
        ) {

}
