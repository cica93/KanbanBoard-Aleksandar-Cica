package com.example.Kanban.Board.utilities;

import com.example.Kanban.Board.service.NotificationSocket;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Unremovable
public class NotificationService {

    private final NotificationSocket notificationSocket;

    public NotificationService(NotificationSocket notificationSocket) {
        this.notificationSocket = notificationSocket;
    }

    public void notifyChange(EntityChangeEvent event) {
        System.out.println("NotificationService.notifyChange: " + event.getType() + " -> " + JsonUtils.toJson(event.getEntity()));
        notificationSocket.broadcast(event);
    }
}