package com.example.Kanban.Board.utilities;

import com.example.Kanban.Board.dto.UserDTO;
import com.example.Kanban.Board.model.User;

public class UserConverter extends GenericConverter<User, UserDTO> {

    @Override
    public UserDTO convertModelToDTOModel(User model) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(model.getId());
        userDTO.setEmail(model.getEmail());
        userDTO.setFullName(model.getFullName());
        userDTO.setToken(model.getPassword());
        userDTO.setToken(model.getToken());
        return userDTO;
    }

    @Override
    public User convertDTOModelToModel(UserDTO dtoModel) {
        User user = new User();
        user.setId(dtoModel.getId());
        user.setEmail(dtoModel.getEmail());
        user.setFullName(dtoModel.getFullName());
        user.setToken(dtoModel.getPassword());
        user.setToken(dtoModel.getToken());
        return user;
    }
    
}
