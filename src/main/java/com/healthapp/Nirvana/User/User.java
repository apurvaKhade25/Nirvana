package com.healthapp.Nirvana.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Nirvana_Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Autoincrement my id count
    private Long id;
    private String username;
    private String password; //will be bcrypt
    private String role;//User or Admin
    private String email;
}
