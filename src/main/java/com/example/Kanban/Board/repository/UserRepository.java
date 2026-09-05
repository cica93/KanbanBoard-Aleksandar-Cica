package com.example.Kanban.Board.repository;

import java.util.Optional;
import java.util.Set;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<UserDTO> findByToken(String token) {
        return find("token", token).project(UserDTO.class).firstResultOptional();
    }

    public PanacheQuery<UserDTO> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String email,
            String fullName, Sort sort) {

        String normalizedEmail = email == null ? "" : email.toLowerCase();
        String normalizedFullName = fullName == null ? "" : fullName.toLowerCase();

        return find(
            "LOWER(email) LIKE ?1 OR LOWER(fullName) LIKE ?2", sort,
                "%" + normalizedEmail + "%",
                "%" + normalizedFullName + "%"
        ).project(UserDTO.class);
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