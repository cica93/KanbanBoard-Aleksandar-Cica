package com.example.Kanban.Board.repository;

import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.example.Kanban.Board.model.User;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

     public Optional<User> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public PanacheQuery<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String email,
            String fullName, Sort sort) {

        return find(
            "LOWER(email) LIKE ?1 OR LOWER(fullName) LIKE ?2", sort,
            "%" + email.toLowerCase() + "%",
            "%" + fullName.toLowerCase() + "%"
        );
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

    public long countByIdIn(Set<Long> ids) {

        return count("id in ?1", ids);
    }
}