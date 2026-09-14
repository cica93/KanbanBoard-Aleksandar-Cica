package com.example.Kanban.Board.configuration;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

@ApplicationScoped
public class JdbiProducer {

    @Produces
    @ApplicationScoped
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource);
    }
}
