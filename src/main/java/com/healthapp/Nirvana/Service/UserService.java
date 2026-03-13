package com.healthapp.Nirvana.Service;

import com.healthapp.Nirvana.Model.LoginRequest;
import com.healthapp.Nirvana.Model.User;
import com.healthapp.Nirvana.Repo.UserRepo;
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
        user.setRole("USER"); //default set role
        userRepo.save(user); //save user in db
        return user;
    }

    //login
    public String verify(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate( //chcks username exists and password
                    // check
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            if (authentication.isAuthenticated()) { //if valid then generate token
                return jwtService.generateToken(request.getUsername());

            }
            return "fail";

        } catch (AuthenticationException e) {
            return "invalid username and password";
        }
    }
}
