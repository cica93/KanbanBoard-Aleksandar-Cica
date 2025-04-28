package com.example.Kanban.Board.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
        if (arg0 == null || arg0.length() < 6) {
            return false;
        }
        return arg0.matches(".*[^a-zA-Z0-9 ].*") && arg0.chars().anyMatch(Character::isUpperCase) && arg0.chars().anyMatch(Character::isDigit) && arg0.chars().anyMatch(Character::isLowerCase);
    }

}
