package com.healthapp.Nirvana.Repo;

import com.healthapp.Nirvana.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.InterfaceAddress;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    User findByUsername(String username);

    
}
