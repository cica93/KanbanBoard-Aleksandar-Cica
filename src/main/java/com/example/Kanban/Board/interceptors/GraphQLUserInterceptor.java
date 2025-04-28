package com.example.Kanban.Board.interceptors;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.repository.UserRepository;

import reactor.core.publisher.Mono;

@Component
public class GraphQLUserInterceptor implements WebGraphQlInterceptor {

    private final UserRepository userRepository;

    public GraphQLUserInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String token = request.getHeaders().getFirst("token");
        return userRepository.findByToken(token).map(user -> {
            request.configureExecutionInput((executionInput, builder)
                -> builder.graphQLContext(ctx -> ctx.put("user", user)).build()
             );
            return chain.next(request);
        }).orElseThrow(() -> new ForbiddenMethodException("Not valid token"));

    }
}
