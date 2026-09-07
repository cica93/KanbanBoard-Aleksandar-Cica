package com.example.Kanban.Board.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.hibernate.query.NativeQuery;

import com.example.Kanban.Board.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public UserRepository(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    private Stream<User> findByEmailOrFullNameLike(String email, String fullName, boolean includePassword) {
        String sql = "SELECT * FROM user WHERE (:email IS NULL OR email = :email) AND (:fullName IS NULL OR LOWER(full_name) LIKE LOWER(CONCAT('%',:fullName,'%')))";
        NativeQuery<User> query = entityManager.createNativeQuery(sql).unwrap(NativeQuery.class);
        query.setParameter("email", email);
        query.setParameter("fullName", fullName);
        query.setTupleTransformer((tuple, aliases) -> {
            User dto = new User();
            dto.setId(((Number) tuple[0]).longValue());
            dto.setFullName((String) tuple[1]);
            dto.setEmail((String) tuple[2]);
            if (includePassword) {
                dto.setPassword((String) tuple[3]);
            }
            dto.setToken((String) tuple[4]);
            try {
                dto.setImage(this.objectMapper.writeValueAsBytes(tuple[5]));
            } catch (JsonProcessingException ex) {
                System.getLogger(UserRepository.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            return dto;
        });
        return query.getResultStream();
    }


    public Optional<User> findByEmail(String email) {
        return this.findByEmailOrFullNameLike(email, "", true).findFirst();
    }

    public List<User> findByFullNameContainingIgnoreCase(
            String fullName) {
        return findByEmailOrFullNameLike(null, fullName, false).toList();
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