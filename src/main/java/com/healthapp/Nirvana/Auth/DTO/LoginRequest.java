package com.healthapp.Nirvana.Auth.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String password;
    private String email;

}
