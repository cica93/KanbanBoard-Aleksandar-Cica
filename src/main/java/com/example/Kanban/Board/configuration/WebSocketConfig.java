package com.example.Kanban.Board.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${fronted.app.port}")
    private Integer port;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure a message broker that supports "simple" messaging for WebSockets.
        config.enableSimpleBroker("/tasks");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint for clients to connect to
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:"+ port).withSockJS();
      
    }
}