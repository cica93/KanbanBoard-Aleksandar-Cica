package com.example.Kanban.Board.utilities;

public class EntityChangeEvent {
    private String type;
    private Object entity;

    public EntityChangeEvent() {}

    public EntityChangeEvent(String type, Object entity) {
        this.type = type;
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public Object getEntity() {
        return entity;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }   
}
