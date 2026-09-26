package com.example.Kanban.Board.exceptions;

import java.util.Arrays;

public class InvalidEnumValueException extends RuntimeException {

    public InvalidEnumValueException(String value, Class<? extends Enum<?>> enumClass) {
        super("Invalid value '" + value + "' for " + enumClass.getSimpleName()
                + ". Allowed values: " + String.join(", ",
                        Arrays.stream(enumClass.getEnumConstants())
                                .map(Enum::name)
                                .toList()));
    }
}
