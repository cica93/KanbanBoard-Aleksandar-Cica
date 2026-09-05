package com.example.Kanban.Board.model;

import com.example.Kanban.Board.utilities.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskPriority {
    LOW,
    MED,
    HIGH;

    @JsonValue
    public String toJson() {
        return name();
    }

    @JsonCreator
    public static TaskPriority fromJson(String value) {
        return EnumUtils.valueOf(TaskPriority.class, value);
    }
}
