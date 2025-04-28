package com.example.Kanban.Board.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;
import com.example.Kanban.Board.utilities.UserConverter;

import jakarta.persistence.criteria.Predicate;

@Service
public class UserService {
    
    private UserConverter userConverter;
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public ResponseEntity<Page<UserDTO>> get(Pageable pageable, String keyword) {
         Specification<User> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
             if (keyword != null && !keyword.isBlank()) {
                String regex = ("%" + keyword + "%").toLowerCase();
				Predicate whereClause = criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), regex)
						, criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), regex));
				predicates.add(whereClause);
			}
			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
         };
     
        Page<UserDTO> data = userRepository.findAll(spec, pageable).map(u -> userConverter.convertModelToDTOModel(u));
        return ResponseEntity.ok(data);
    }
}
