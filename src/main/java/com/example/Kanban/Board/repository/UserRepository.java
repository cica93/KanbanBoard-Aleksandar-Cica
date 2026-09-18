package com.example.Kanban.Board.repository;

import java.util.List;
import java.util.Optional;

import com.example.Kanban.Board.dto.PageResponse;
import com.example.Kanban.Board.filters.Filter;
import com.example.Kanban.Board.filters.Operator;
import com.example.Kanban.Board.filters.PredicateCreator;
import com.example.Kanban.Board.model.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PageResponse<User> findByFilters(List<Filter> filters, Integer limit, Integer offset, String columnSort,
            String direction, boolean includeCountingQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Long total = 0L;
        String notNullSort = columnSort == null ? "id" : columnSort;

        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> userRoot = query.from(User.class);

        query.select(
                cb.construct(
                        User.class,
                        userRoot.get("id"),
                        userRoot.get("email"),
                        userRoot.get("fullName"),
                        userRoot.get("token"),
                        userRoot.get("password")));

        if (includeCountingQuery) {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot)).where(PredicateCreator.buildPredicates(cb, countRoot, filters));
            total = entityManager.createQuery(countQuery).getSingleResult();
        }

        query.where(PredicateCreator.buildPredicates(cb, userRoot, filters));
        query.orderBy(
                "desc".equalsIgnoreCase(direction)
                ? cb.desc(userRoot.get(notNullSort))
                : cb.asc(userRoot.get(notNullSort)));

        List<User> users = entityManager
                .createQuery(query)
                .setFirstResult(offset == null ? 0 : offset)
                .setMaxResults(limit == null ? 20 : limit)
                .getResultList();
        return new PageResponse<>(users, total);
    }

    public Optional<User> findByEmail(String email) {
        return findByFilters(List.of(new Filter("email", Operator.EQUALS,
                email)),
                1, 0, null, null, false).getContent().stream().findFirst();
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