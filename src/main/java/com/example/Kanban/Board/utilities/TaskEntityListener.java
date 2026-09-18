package com.example.Kanban.Board.utilities;

import java.util.Map;

import com.example.Kanban.Board.model.Payload;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class TaskEntityListener {

    private NotificationService notificationService() {
        return CDI.current()
                .select(NotificationService.class)
                .get();
    }

    @PostPersist
    public void onCreate(Object entity) {
        notificationService()
                .notifyChange(createEvent(entity, "CREATED"));
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        notificationService()
                .notifyChange(createEvent(entity, "UPDATED"));
    }

    @PostRemove
    public void onDelete(Object entity) {
        notificationService()
                .notifyChange(createEvent(entity, "DELETED"));
    }

    private EntityChangeEvent createEvent(Object entity, String type) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Payload payload) {
            return new EntityChangeEvent(type, payload.createPayload());
        }
        return new EntityChangeEvent(type, Map.of("entityType", entity.getClass().getSimpleName()));
    }
}