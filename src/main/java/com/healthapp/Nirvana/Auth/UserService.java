package com.healthapp.Nirvana.Auth;

import com.healthapp.Nirvana.Auth.DTO.LoginRequest;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    //register
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));//encode password
        System.out.println(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user); //save user in db
        return user;
    }

    //login
    public String login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                User user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
                return jwtService.generateToken(user.getEmail(), user.getRole());
            }
            return "fail";
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
