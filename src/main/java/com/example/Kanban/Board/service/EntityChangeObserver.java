package com.example.Kanban.Board.service;

import com.example.Kanban.Board.utilities.EntityChangeEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class EntityChangeObserver {

    private final NotificationSocket socket;

    public EntityChangeObserver(NotificationSocket socket) {
        this.socket = socket;
    }


    public void onChange(
        @Observes EntityChangeEvent event
    ) {
        socket.broadcast(event);
    }
}
