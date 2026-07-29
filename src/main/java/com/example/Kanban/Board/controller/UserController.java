package com.example.Kanban.Board.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.service.UserService;
import com.example.Kanban.Board.utilities.UserConverter;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;

    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> get(
           @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "offset", required = false) Integer offset,
            @RequestParam(name = "column", required = false) String column,
            @RequestParam(name = "limit", required = false) Integer limit,
            @RequestParam(name = "keyword", required = false) String keyword) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC, column);
        Pageable pageable = PageRequest.of(offset / limit, limit, sort);    
        return userService.get(pageable, keyword);
    }
    
    @GetMapping("/current")
    public ResponseEntity<UserDTO> currentUser(User user) {
        return ResponseEntity.ok(userConverter.convertModelToDTOModel(user));
    }

    @GetMapping("/has-mail/{email}")
    public ResponseEntity<Boolean> hasMail(@PathVariable String email) {
        return userService.hasMail(email);
    }

}
