package com.example.Kanban.Board.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.Kanban.Board.dto.PageResponse;
import com.example.Kanban.Board.filters.FilterModel;
import com.example.Kanban.Board.filters.PredicateCreator;
import com.example.Kanban.Board.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public UserRepository(EntityManager entityManager, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }

    public PageResponse<Map<String, Object>> findByFilters(String filterJson, Integer limit, Integer offset, String columnSort,
            String direction, boolean includeCountingQuery) {
        return findByFilters(PredicateCreator.parseFilters(filterJson, objectMapper), limit, offset, columnSort,
                direction, includeCountingQuery);
    }

    public PageResponse<Map<String, Object>> findByFilters(Map<String, FilterModel> filter, Integer limit, Integer offset, String columnSort,
            String direction, boolean includeCountingQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Long total = 0L;
        String notNullSort = columnSort == null ? "id" : columnSort;

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<User> userRoot = query.from(User.class);

        query.multiselect(
                userRoot.get("id").alias("id"),
                userRoot.get("email").alias("email"),
                userRoot.get("fullName").alias("fullName"),
                userRoot.get("token").alias("token"),
                userRoot.get("password").alias("password"));

        if (includeCountingQuery) {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot)).where(PredicateCreator.buildFilters(cb, countRoot, filter));
            total = entityManager.createQuery(countQuery).getSingleResult();
        }

        query.where(PredicateCreator.buildFilters(cb, userRoot, filter));
        query.orderBy(
                "desc".equalsIgnoreCase(direction)
                ? cb.desc(userRoot.get(notNullSort))
                : cb.asc(userRoot.get(notNullSort)));

         List<Tuple> users = entityManager
                .createQuery(query)
                .setFirstResult(offset == null ? 0 : offset)
                .setMaxResults(limit == null ? 20 : limit)
                .getResultList();
               
        return new PageResponse<>(this.convert(users), total);
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

   private List<Map<String, Object>> convert(List<Tuple> tuples) {
      return tuples.stream()
        .map(tuple -> Map.of(
                "id", tuple.get("id"),
                "fullName", tuple.get("fullName"),
                "email", tuple.get("email"),
                "password", tuple.get("password")
        ))
        .toList();
   }

    @Transactional
    public int saveToken(Long id, String token) {

        return update(
                "token = ?1 where id = ?2",
                token,
                id);
    }
    
    @Transactional
    public User save(User user) {
        persist(user);
        flush();
        return user;
    }
}