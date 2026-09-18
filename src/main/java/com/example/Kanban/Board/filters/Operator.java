package com.example.Kanban.Board.filters;

public enum Operator {

    EQUALS("equals"),
    NOT_EQUALS("notEqual"),
    CONTAINS("contains"),
    NOT_CONTAINS("notContains"),
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith"),
    GREATER_THAN("greaterThan"),
    LESS_THAN("lessThan"),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),
    IS_NULL("blank"),
    NOT_NULL("notBlank");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Operator fromValue(String value) {
        for (Operator operator : values()) {
            if (operator.value.equals(value)) {
                return operator;
            }
        }

        throw new IllegalArgumentException(
                "Unknown operator: " + value
        );
    }
}
