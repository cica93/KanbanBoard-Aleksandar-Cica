package com.example.Kanban.Board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDTO {

    private Long id;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private String token;
    private byte[] image;

    public UserDTO() {
    }

    public UserDTO(Long id, String fullName, String email, String password, String token, byte[] image) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.token = token;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
