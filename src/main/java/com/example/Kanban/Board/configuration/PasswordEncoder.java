package com.example.Kanban.Board.configuration;

public interface PasswordEncoder {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
