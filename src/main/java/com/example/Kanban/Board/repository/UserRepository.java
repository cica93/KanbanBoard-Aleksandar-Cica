package com.example.Kanban.Board.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.Kanban.Board.dto.PageResponse;
import com.example.Kanban.Board.model.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private PageResponse<User> findAllByEmailOrFullNameLike(String email, String fullName, Integer limit, Integer offset, String columnSort, String direction) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Long total = 0L;

        CriteriaQuery<User> query
                = cb.createQuery(User.class);

        Root<User> userRoot = query.from(User.class);

        query.select(
                cb.construct(
                        User.class,
                        userRoot.get("id"),
                        userRoot.get("email"),
                        userRoot.get("fullName"),
                        userRoot.get("token"),
                        userRoot.get("password")
                )
        );

        if (email != null) {
            query.where(
                    cb.equal(userRoot.get("email"), email));
        } else {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(cb.count(countRoot)).where(buildPredicates(cb, countRoot, fullName).toArray(Predicate[]::new));
            query.where(buildPredicates(cb, userRoot, fullName).toArray(Predicate[]::new));
            query.orderBy(
                    "desc".equalsIgnoreCase(direction)
                            ? 
                            cb.desc(getSortRoot(userRoot,columnSort))
                            : 
                            cb.asc(getSortRoot(userRoot,columnSort))
                );
           

            total = entityManager
                    .createQuery(countQuery)
                    .getSingleResult();
        }

        List<User> users = entityManager
                        .createQuery(query)
                        .setFirstResult(offset == null ? 0 : offset)
                        .setMaxResults(limit == null ? 20: limit)
                        .getResultList();
        return new PageResponse<>(users, total);
    }


    public Optional<User> findByEmail(String email) {
        return this.findAllByEmailOrFullNameLike(email, null, 1, 0, null, null).getContent().stream().findFirst();
    }

    public PageResponse<User> findByFullNameContainingIgnoreCase(
            String fullName, Integer limit, Integer offset, String columnSort, String direction) {
        return findAllByEmailOrFullNameLike(null, fullName, limit, offset, columnSort, direction);
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

   private List<Predicate> buildPredicates(CriteriaBuilder cb,Root<User> userRoot, String search) {

    List<Predicate> predicates = new ArrayList<>();

    if (search != null && !search.isBlank()) {

        String pattern = "%" + search.trim().toLowerCase() + "%";

        predicates.add(
                cb.like(
                    cb.lower(userRoot.get("fullName")),
                    pattern
                )
        );
    }

    return predicates;
}
    
    private Path<Object> getSortRoot(Root<User> userRoot, String columnSort) {
        return userRoot.get(columnSort == null ? "id" : columnSort);
    }
}