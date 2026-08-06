package com.example.Kanban.Board.utilities;

import com.example.Kanban.Board.model.Task;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import java.util.HashMap;
import java.util.Map;

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
        if (entity instanceof Task task) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", task.getId());
            payload.put("version", task.getVersion());
            payload.put("title", task.getTitle());
            payload.put("description", task.getDescription());
            if (task.getTaskStatus() != null) {
                payload.put("taskStatus", task.getTaskStatus().name());
            }
            if (task.getTaskPriority() != null) {
                payload.put("taskPriority", task.getTaskPriority().name());
            }
            if (task.getCreatedBy() != null) {
                payload.put("createdBy", task.getCreatedBy());
            }
            if (task.getUpdatedBy() != null) {
                payload.put("updatedBy", task.getUpdatedBy());
            }
            payload.put("taskOrder", task.getTaskOrder());
            return new EntityChangeEvent(type, payload);
        }

        return new EntityChangeEvent(type, Map.of("entityType", entity.getClass().getSimpleName()));
    }
}