package com.healthapp.Nirvana.Auth;

import com.healthapp.Nirvana.Exception.ResourceNotFoundException;
import com.healthapp.Nirvana.User.User;
import com.healthapp.Nirvana.User.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepo.findByUsername((username))
//                        .orElseThrow(()->new ResourceNotFoundException("User not found"));
//        System.out.println("Searching user: " + username);
//        if (user == null) {
//            System.out.println("user not found");
//            throw new UsernameNotFoundException("user not found");
//        }
//        return new UserPrinciple(user);
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail((username))
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        System.out.println("Searching user: " + user);
        if (user == null) {
            System.out.println("user not found");
            throw new UsernameNotFoundException("user not found");
        }
        return new UserPrinciple(user);
    }
}
