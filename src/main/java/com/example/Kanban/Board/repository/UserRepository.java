package com.example.Kanban.Board.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    private Stream<User> findAllByEmailOrFullNameLike(String email, String fullName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

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
            query.where(
                    cb.like(userRoot.get("fullName"), "%" + fullName + "%"));
            query.orderBy(
                    cb.asc(userRoot.get("id"))
            );
        }

        return entityManager
                .createQuery(query).getResultStream();
    }


    public Optional<User> findByEmail(String email) {
        return this.findAllByEmailOrFullNameLike(email, null).findFirst();
    }

    public List<User> findByFullNameContainingIgnoreCase(
            String fullName) {
        return findAllByEmailOrFullNameLike(null, fullName).toList();
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