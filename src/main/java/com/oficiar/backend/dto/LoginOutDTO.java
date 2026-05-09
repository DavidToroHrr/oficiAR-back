package com.oficiar.backend.dto;

// En tu Spring Boot
public class LoginOutDTO {
    private String token;
    private String name;
    private String email;
    public LoginOutDTO(String token, String name, String email) {
        this.token = token;
        this.name = name;
        this.email = email;
    }
    // Agrega los Getters y Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}