package com.example.Kanban.Board.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Kanban.Board.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByToken(String token);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.token = ?2 WHERE u.id = ?1")
    int saveToken(Long id, String token);

    Long countByIdIn(Set<Long> ids);

}
