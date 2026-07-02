package com.healthapp.Nirvana.Auth;

import com.healthapp.Nirvana.Auth.DTO.LoginRequest;
import com.healthapp.Nirvana.Auth.DTO.RegisterRequest;
import com.healthapp.Nirvana.User.Role;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userservice;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        //create new user
        if (registerRequest == null) {
            return "Invalid request data";
        }
        if (registerRequest.getRole() == null || registerRequest.getRole().isEmpty()) {
            return "Role is required";
        }
        if (userRepo.findByEmail(registerRequest.getEmail()).isPresent()) {
            return "Email already exists";
        }
        if(userRepo.findByUsername(registerRequest.getUsername()).isPresent()) {
            return "Username already exists";
        }

        //token for new user
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Role role;
        try {
            role = Role.valueOf(registerRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid role. Valid roles are: PATIENT, DOCTOR";
        }
        newUser.setRole(role);
        userRepo.save(newUser);

        return jwtService.generateToken(newUser.getEmail(), newUser.getRole());
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userservice.login(request);
    }
}
