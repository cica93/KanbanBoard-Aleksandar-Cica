package com.example.Kanban.Board.model;

import com.example.Kanban.Board.utilities.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    DONE;

    @JsonValue
    public String toJson() {
        return name().replace("_", " ");
    }

    @JsonCreator
    public static TaskStatus fromJson(String value) {
        return EnumUtils.valueOf(TaskStatus.class, value.replace(" ", "_"));
    }
}
