package com.example.Kanban.Board.filters;

import java.util.Arrays;

public enum FilterType {
    EQUALS("equals"),
    NOT_EQUAL("notEqual"),
    CONTAINS("contains"),
    NOT_CONTAINS("notContains"),
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith"),
    BLANK("blank"),
    GREATHER_THEN("greaterThan"),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual"),
    LESS_THAN("lessThan"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),
    BETWEEN("between"),
    IN_RANGE("inRange"),
    NOT_BLANK("notBlank");

    private final String value;

    FilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FilterType fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported filter type: " + value));
    }
}
