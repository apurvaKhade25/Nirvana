package com.healthapp.Nirvana.Auth;

import com.healthapp.Nirvana.Auth.DTO.LoginRequest;
import com.healthapp.Nirvana.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userservice;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userservice.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userservice.verify(request);
    }
}
