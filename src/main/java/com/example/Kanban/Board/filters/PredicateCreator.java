package com.example.Kanban.Board.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PredicateCreator {

    private static <T> Predicate buildPredicate(
            CriteriaBuilder cb,
            Root<T> root,
            String field,
            FilterModel filter) {
        if (filter.conditions() != null && !filter.conditions().isEmpty()) {
            List<Predicate> predicates = filter.conditions()
                    .stream()
                    .map(condition -> buildPredicate(cb, root, field, condition))
                    .toList();

            return "OR".equalsIgnoreCase(filter.operator())
                    ? cb.or(predicates.toArray(Predicate[]::new))
                    : cb.and(predicates.toArray(Predicate[]::new));
        }

        Path<?> path = root.get(field);
        FilterType filterType = FilterType.fromValue(filter.type());


        return switch (filterType) {
            case EQUALS ->
                buildEquals(cb, path, filter.filter());

            case NOT_EQUAL ->
                cb.not(buildEquals(cb, path, filter.filter()));

            case CONTAINS ->
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filter.filter().toString().toLowerCase() + "%");

            case NOT_CONTAINS ->
                cb.not(
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filter.filter().toString().toLowerCase() + "%"));

            case STARTS_WITH ->
                cb.like(
                cb.lower(path.as(String.class)),
                filter.filter().toString().toLowerCase() + "%");

            case ENDS_WITH ->
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filter.filter().toString().toLowerCase());

            case BLANK ->
                cb.or(
                cb.isNull(path),
                cb.equal(cb.trim(path.as(String.class)), ""));

            case NOT_BLANK ->
                cb.and(
                cb.isNotNull(path),
                cb.notEqual(cb.trim(path.as(String.class)), ""));

            default ->
                throw new IllegalArgumentException(
                        "Unsupported filter type: " + filter.type());
        };
    }

        
    private static Predicate buildEquals(
        CriteriaBuilder cb,
        Path<?> path,
        Object value) {
        Class<?> javaType = path.getJavaType();

        if (value instanceof Collection<?> values) {

                CriteriaBuilder.In<Object> in = cb.in(path);

                if (javaType.isEnum()) {
                        values.forEach(v -> in.value(toEnum(javaType, v.toString())));
                } else {
                        values.forEach(in::value);
                }

                return in;
        }

        if (javaType.isEnum()) {
                return cb.equal(
                                path,
                                toEnum(javaType, value.toString()));
        }

                return cb.equal(path, value);
        }

        @SuppressWarnings({"rawtypes","unchecked"})
        private static Enum<?> toEnum(
                Class<?> enumType,
                String value) {
                return Enum.valueOf(
                        (Class<? extends Enum>) enumType,
                        value
                );
        }

    public static <T> Predicate buildFilters(
            CriteriaBuilder cb,
            Root<T> root,
            Map<String, FilterModel> filters
    ) {
        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((field, filter) -> {
            predicates.add(PredicateCreator.buildPredicate(cb, root, field, filter));
        });

        return cb.and(
                predicates.toArray(Predicate[]::new));
    }

    public static <T> Predicate buildFilters(
            CriteriaBuilder cb,
            Root<T> root,
            String filters,
            ObjectMapper objectMapper
    ) {
        Map<String, FilterModel> parsed = parseFilters(filters, objectMapper);
        return PredicateCreator.buildFilters(cb, root, parsed);
    }

    public static Map<String, FilterModel> parseFilters(String filterJson, ObjectMapper objectMapper) {
        if (filterJson == null || filterJson.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(
                    filterJson,
                    new TypeReference<Map<String, FilterModel>>() {
            }
            );
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Invalid AG Grid filter JSON",
                    e
            );
        }
    }
}
