package com.example.Kanban.Board.configuration;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
public class JdbiProducer {

    @Produces
    @ApplicationScoped
    public Jdbi produceJdbi(DataSource dataSource) {
        Jdbi jdbi = Jdbi.create(dataSource);

        jdbi.installPlugin(new SqlObjectPlugin());

        return jdbi;
    }
}
