package com.example.Kanban.Board.interceptors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.Kanban.Board.repository.UserRepository;

@Configuration
public class ArgResolvers implements WebMvcConfigurer {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageRequestResolver());
        resolvers.add(new UserResolver(userRepository));
    }
}
