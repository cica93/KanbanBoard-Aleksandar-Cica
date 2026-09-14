package com.example.Kanban.Board.configuration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    private final String allowedOrigin;

    public CorsFilter(@ConfigProperty(name = "frontend.app.port") Integer frontendAppPort) {
        this.allowedOrigin = "http://localhost:" + frontendAppPort;
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {

        String path = requestContext.getUriInfo().getPath();

        if (!(path.startsWith("/app/")
                || path.startsWith("/ws/")
                || path.startsWith("/topic/"))) {
            return;
        }

        var headers = responseContext.getHeaders();

        headers.putSingle("Access-Control-Allow-Origin", allowedOrigin);
        headers.putSingle(
                "Access-Control-Allow-Methods",
                "GET,POST,PUT,DELETE,PATCH,OPTIONS");
        headers.putSingle("Access-Control-Allow-Headers", "*");
        headers.putSingle("Access-Control-Allow-Credentials", "true");
    }
}
