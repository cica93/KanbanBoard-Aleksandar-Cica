package com.example.Kanban.Board.configuration;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public boolean matches(
            String rawPassword,
            String encodedPassword) {

        return BCrypt.checkpw(
                rawPassword,
                encodedPassword
        );
    }
}
