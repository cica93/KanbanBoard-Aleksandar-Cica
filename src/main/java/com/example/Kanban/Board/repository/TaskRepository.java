package com.example.Kanban.Board.repository;


import com.example.Kanban.Board.model.Task;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;




@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {


}
