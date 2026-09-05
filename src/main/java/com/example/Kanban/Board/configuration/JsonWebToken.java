package com.example.Kanban.Board.configuration;

import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonWebToken {
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
