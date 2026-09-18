package com.example.Kanban.Board.filters;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PredicateCreator {

   public static List<Filter> convertFilters(
        Map<String, AgGridFilter> filterModel) {

    if (filterModel == null || filterModel.isEmpty()) {
        return List.of();
    }

    return filterModel.entrySet()
            .stream()
            .map(entry -> {

                String field = entry.getKey();
                AgGridFilter agFilter = entry.getValue();

                return new Filter(
                        field,
                        Operator.fromValue(
                                agFilter.type()
                        ),
                        agFilter.filter()
                );
            })
            .toList();
    }
    public static Predicate[] buildPredicates(CriteriaBuilder cb, Root<?> root, List<Filter> filters) {
        return filters.stream()
                .filter(Objects::nonNull)
                .map(filter -> {
                    return switch (filter.getOperator()) {
                case EQUALS -> 
                    cb.equal(
                            root.get(filter.getField()),
                            filter.getValue()
                            );
                case NOT_EQUALS -> 
                    cb.notEqual(
                            root.get(filter.getField()),
                            filter.getValue()
                    );
                    
                        case CONTAINS ->
                    cb.like(
                            cb.lower(
                                    root.get(filter.getField())
                            ),
                            "%" + filter.getValue()
                                    .toString()
                                    .toLowerCase() + "%"
                            );
                        case NOT_CONTAINS ->
                            cb.notLike(
                            cb.lower(
                            root.get(filter.getField())
                            ),
                            "%" + filter.getValue()
                            .toString()
                            .toLowerCase() + "%"
                            );
                    
                case STARTS_WITH ->
                    cb.like(
                            cb.lower(
                                    root.get(filter.getField())
                            ),
                            filter.getValue()
                                    .toString()
                                    .toLowerCase() + "%"
                            );
                    
                case ENDS_WITH ->
                    cb.like(
                            cb.lower(
                                    root.get(filter.getField())
                            ),
                            "%"+ filter.getValue()
                                    .toString()
                                    .toLowerCase()
                    );

                        case IS_NULL ->
                            cb.isNull(root.get(filter.getField()));
                        case NOT_NULL ->
                            cb.isNotNull(root.get(filter.getField()));

                case GREATER_THAN ->
                    cb.greaterThan(
                            root.get(filter.getField()).as(Comparable.class),
                            (Comparable) filter.getValue()
                    );

                case LESS_THAN ->
                    cb.lessThan(
                            root.get(filter.getField()).as(Comparable.class),
                            (Comparable) filter.getValue()
                    );

                case GREATER_THAN_OR_EQUAL ->
                    cb.greaterThanOrEqualTo(
                            root.get(filter.getField()).as(Comparable.class),
                            (Comparable) filter.getValue()
                    );

                case LESS_THAN_OR_EQUAL ->
                    cb.lessThanOrEqualTo(
                            root.get(filter.getField()).as(Comparable.class),
                            (Comparable) filter.getValue()
                    );
                    };
                }).toArray(Predicate[]::new);
    }
}
