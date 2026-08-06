package com.example.Kanban.Board.configuration;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {


        String path = requestContext.getUriInfo().getPath();

        if (path.startsWith("/app/") || path.startsWith("/ws/") || path.startsWith("/topic/")) {
            responseContext.getHeaders()
                    .add("Access-Control-Allow-Origin", "http://localhost:4200");

            responseContext.getHeaders()
                    .add("Access-Control-Allow-Methods",
                            "GET,POST,PUT,DELETE,PATCH,OPTIONS");

            responseContext.getHeaders()
                    .add("Access-Control-Allow-Headers", "*");

            responseContext.getHeaders()
                    .add("Access-Control-Allow-Credentials", "true");
        }
    }
}