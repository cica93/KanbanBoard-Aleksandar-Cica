package com.example.Kanban.Board.interceptors;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;

public class PageRequestResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String[] column = webRequest.getParameterValues("column");
        String order = webRequest.getParameter("order");
        String offset = webRequest.getParameter("offset");
        String limit = webRequest.getParameter("limit");
        if (limit == null) {
            limit = "20";
        }
        if (offset == null) {
            offset = "0";
        }
        if (column == null) {
            column = new String[] { "id" };
        }
        try {
            Sort sort = Sort.by("desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, column);
            return PageRequest.of(Integer.parseInt(offset) / Integer.parseInt(limit), Integer.parseInt(limit), sort);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid argument for page or page size");
        }

    }

}
