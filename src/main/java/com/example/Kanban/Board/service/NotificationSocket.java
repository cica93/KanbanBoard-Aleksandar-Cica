package com.example.Kanban.Board.service;

import com.example.Kanban.Board.utilities.JsonUtils;

import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket(path = "/ws")
@ApplicationScoped
public class NotificationSocket {

    private final Set<WebSocketConnection> connections =
            ConcurrentHashMap.newKeySet();


    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);

        System.out.println(
            "Client connected: " + connection.id()
        );

        try {
            connection.sendTextAndAwait(JsonUtils.toJson(Map.of(
                    "type", "CONNECTED",
                    "message", "WebSocket connection established"
            )));
        } catch (Exception e) {
            System.out.println("Failed to send CONNECTED message to " + connection.id() + ": " + e.getMessage());
        }
    }


    @OnClose
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);

        System.out.println(
            "Client disconnected: " + connection.id()
        );
    }


    public void broadcast(Object payload) {
        String json = JsonUtils.toJson(payload);
        System.out.println("Broadcasting websocket payload to " + connections.size() + " connections: " + json);
        connections.forEach(connection -> {
            try {
                connection.sendTextAndAwait(json);
            } catch (Exception e) {
                System.out.println("Failed to broadcast to " + connection.id() + ": " + e.getMessage());
            }
        });
    }
}
