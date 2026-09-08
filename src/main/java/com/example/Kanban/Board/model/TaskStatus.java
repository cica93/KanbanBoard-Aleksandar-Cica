package com.example.Kanban.Board.model;

import com.example.Kanban.Board.utilities.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    TO_DO("TO DO"),
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String toJson() {
        return value;
    }

    @JsonCreator
    public static TaskStatus fromJson(String value) {
        return EnumUtils.valueOf(TaskStatus.class, value.replace(" ", "_"));
    }
}
