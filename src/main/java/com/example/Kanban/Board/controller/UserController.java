package com.example.Kanban.Board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.UserService;
import com.example.Kanban.Board.utilities.UserConverter;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private UserConverter userConverter;
    
    @Autowired
    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> get(Pageable pageable,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return userService.get(pageable, keyword);
    }
    
    @GetMapping("/current")
    public ResponseEntity<UserDTO> currentUser(User user) {
        return ResponseEntity.ok(userConverter.convertModelToDTOModel(user));
    }


}
