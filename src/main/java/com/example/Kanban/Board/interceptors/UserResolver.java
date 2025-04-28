package com.example.Kanban.Board.interceptors;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;

public class UserResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public UserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class)
                && parameter.getParameterAnnotation(RequestBody.class) == null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader("token");
        return userRepository.findByToken(token).map(user -> {
            return user;
        }).orElseThrow(() -> new ForbiddenMethodException("Not valid token"));
    }
}

