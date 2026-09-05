package com.example.Kanban.Board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDTOWithCredentials extends UserDTO {
    
    private String token;
    
    @JsonIgnore
    private String password;

    public UserDTOWithCredentials() {
        super();
    }

    public UserDTOWithCredentials(
            Long id,
            String fullName,
            String email,
            String password,
            String token,
            byte[] image
    ) {
        super(id, fullName, email, image);
        this.password = password;
        this.token = token;
        
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
