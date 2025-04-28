package com.example.Kanban.Board.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.Kanban.Board.service.NotificationService;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

@Component
public class TaskEntityListener {

    private NotificationService notificationService;
    
    @Autowired
    public void setNotificationService(NotificationService service) {
        this.notificationService = service;
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    public void onChange(Object task) {
        notificationService.sendEntityUpdate(new TaskMessage(task));
    }

}