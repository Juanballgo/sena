package com.sena.dto;

public class LoginResponse {
    private String id;
    private String names;
    private String lastName;
    private String email;
    private String token;
    private String semillero;

    // Constructor
    public LoginResponse(String message, String id, String names, String lastName, String email, String token, String semillero) {
        this.id = id;
        this.names = names;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
        this.semillero = semillero;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNames() { return names; }
    public void setNames(String names) { this.names = names; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getSemillero() { return semillero; }
    public void setSemillero(String semillero) { this.semillero = semillero; }
}