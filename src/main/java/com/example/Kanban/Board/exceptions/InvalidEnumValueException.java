package com.example.Kanban.Board.exceptions;

public class InvalidEnumValueException extends RuntimeException {

    public InvalidEnumValueException(String value, Class<? extends Enum<?>> enumClass) {
        super("Invalid value '" + value + "' for " + enumClass.getSimpleName()
                + ". Allowed values: " + String.join(", ",
                        java.util.Arrays.stream(enumClass.getEnumConstants())
                                .map(Enum::name)
                                .toList()));
    }
}
