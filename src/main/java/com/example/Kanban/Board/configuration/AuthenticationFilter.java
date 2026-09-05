package com.example.Kanban.Board.configuration;

import java.util.Set;

import com.example.Kanban.Board.utilities.JwtTokenUtil;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of("/api/login");

    private final JwtTokenUtil jwtTokenUtil;
    private final JsonWebToken jsonWebToken;

    public AuthenticationFilter(JwtTokenUtil jwtTokenUtil, JsonWebToken jsonWebToken) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.jsonWebToken = jsonWebToken;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (PUBLIC_PATHS.contains(path) || path.contains("/api/users/has-email")) {
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            abort(requestContext, "Authentication required");
            return;
        }

        try {
            String subject = jwtTokenUtil.extractSubject(authorization);
            this.jsonWebToken.setSubject(subject);
        } catch (SecurityException e) {
            abort(requestContext, "Invalid or expired JWT token");
        }
    }

    private void abort(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"" + message + "\"}")
                .type("application/json")
                .build()
        );
    }
}