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
import jakarta.persistence.criteria.Expression;

import java.time.LocalDate;

public class PredicateCreator {

    @SuppressWarnings("unchecked")
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
        FilterType type = FilterType.fromValue(filter.type());
        boolean isFilterTypeDate = "date".equalsIgnoreCase(filter.filterType());
        Object filterValue = isFilterTypeDate ? filter.dateFrom() : filter.filter();
        return switch (type) {
            case EQUALS ->
                buildEquals(cb, path, filterValue, isFilterTypeDate);

            case NOT_EQUAL ->
                cb.not(buildEquals(cb, path, filterValue, isFilterTypeDate));

            case CONTAINS ->
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filterValue.toString().toLowerCase() + "%");

            case GREATER_THAN_OR_EQUAL -> {
                if (isFilterTypeDate && filterValue instanceof String dateString) {
                    yield cb.greaterThanOrEqualTo(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString));
                }
                yield cb.greaterThanOrEqualTo(
                (Expression<? extends Comparable>) path,
                (Comparable) filterValue);
            }

            case GREATHER_THEN -> {
                if (isFilterTypeDate && filterValue instanceof String dateString) {
                    yield cb.greaterThan(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString));
                }
                yield cb.greaterThan(
                (Expression<? extends Comparable>) path,
                (Comparable) filterValue);
            }

            case LESS_THAN -> {

                if (isFilterTypeDate && filterValue instanceof String dateString) {
                    yield cb.lessThan(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString));
                }
                yield cb.lessThan(
                (Expression<? extends Comparable>) path,
                (Comparable) filterValue);
            }

            case LESS_THAN_OR_EQUAL -> {
                if (isFilterTypeDate && filterValue instanceof String dateString) {
                    yield cb.lessThanOrEqualTo(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString));
                }
                yield cb.lessThanOrEqualTo(
                (Expression<? extends Comparable>) path,
                (Comparable) filterValue);
            }

            case BETWEEN -> {
                List<?> values = (List<?>) filterValue;
                if (values.size() != 2) {
                    throw new IllegalArgumentException(
                            "BETWEEN filter requires exactly two values");
                }
                if (isFilterTypeDate && values.get(0) instanceof String dateString1 && values.get(1) instanceof String dateString2) {
                    yield cb.between(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString1),
                    LocalDate.parse(dateString2));
                }

                yield cb.between(
                (Expression<? extends Comparable>) path,
                (Comparable) values.get(0),
                (Comparable) values.get(1)
                );
            }

            case IN_RANGE -> {
                yield cb.between(
                path.as(LocalDate.class),
                LocalDate.parse(filter.dateFrom()),
                LocalDate.parse(filter.dateTo()));
            }

            case NOT_CONTAINS ->
                cb.not(
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filterValue.toString().toLowerCase() + "%"));

            case STARTS_WITH ->
                cb.like(
                cb.lower(path.as(String.class)),
                filterValue.toString().toLowerCase() + "%");

            case ENDS_WITH ->
                cb.like(
                cb.lower(path.as(String.class)),
                "%" + filterValue.toString().toLowerCase());

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
            Object value,
            boolean isFilterTypeDate) {
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
        if (isFilterTypeDate && value instanceof String dateString) {
            return cb.equal(
                    path.as(LocalDate.class),
                    LocalDate.parse(dateString));
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
