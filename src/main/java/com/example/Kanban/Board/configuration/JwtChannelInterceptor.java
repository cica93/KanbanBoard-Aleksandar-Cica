package com.example.Kanban.Board.configuration;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public JwtChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {

        StompHeaderAccessor accessor
                = MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor != null && accessor.getCommand()!= null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader
                    = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing JWT token");
            }

            String token = authHeader.substring(7);

            Authentication authentication
                    = jwtService.parseToken(token);

            accessor.setUser(authentication);
        }

        return message;
    }
}
