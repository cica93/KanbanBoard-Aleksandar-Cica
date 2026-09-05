package com.example.Kanban.Board.utilities;

import com.example.Kanban.Board.exceptions.InvalidEnumValueException;

public final class EnumUtils {
    public static <T extends Enum<T>> T valueOf(
            Class<T> enumClass,
            String value) {

        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException(value, enumClass);
        }
    }
}
