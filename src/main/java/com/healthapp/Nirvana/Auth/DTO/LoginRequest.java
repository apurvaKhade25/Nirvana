package com.healthapp.Nirvana.Auth.DTO;

public class LoginRequest {
//    private String username;
    private String password;
    private String email;

//    public String getUsername() {
//        return username;
//    }

    public String getPassword() {
        return password;
    }

    public String getEmail() { return email;}

    public void setPassword(String password) {
        this.password = password;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }
}
