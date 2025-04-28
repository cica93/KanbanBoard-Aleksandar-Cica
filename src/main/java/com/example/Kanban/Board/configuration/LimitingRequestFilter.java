package com.example.Kanban.Board.configuration;


import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LimitingRequestFilter extends OncePerRequestFilter {

    @Value("${requests.max.requests.in.one.minute}")
    private Integer requestsInOneMinute;

    @Value("${app.environment}")
    private String appEnvironment;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${spring.graphql.path}")
    private String graphqlPath;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        final Bandwidth limit = Bandwidth.builder()
                .capacity(requestsInOneMinute)
                .refillGreedy(requestsInOneMinute, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURL().toString();
        if (url.startsWith("http://" + appEnvironment + ":" + serverPort + "/api") || url.contains(graphqlPath)) {
                String remoteAddr = request.getRemoteAddr();

            final Bucket bucket = buckets.computeIfAbsent(remoteAddr, k -> createNewBucket());

                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(429);
                    response.getWriter().write("Too many requests");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
   
    }
}
