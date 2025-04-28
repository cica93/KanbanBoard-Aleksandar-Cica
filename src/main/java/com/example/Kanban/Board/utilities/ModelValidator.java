package com.example.Kanban.Board.utilities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class ModelValidator {

    public static <M> Map<String, String> validate(M model, Validator validator) {
        Map<String, String> map = new LinkedHashMap<>();
        Set<ConstraintViolation<M>> errors = validator.validate(model);
        if (!errors.isEmpty()) {
            for (ConstraintViolation<M> e : errors) {
                map.put(e.getPropertyPath().toString(), e.getMessage());
            }
            return map;
        }
        return null;
    }

}
