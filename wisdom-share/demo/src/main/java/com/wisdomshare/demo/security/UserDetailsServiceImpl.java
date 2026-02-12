package com.wisdomshare.demo.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service

public class UserDetailsServiceImpl implements UserDetailsService {
    // Commente la ligne du repository si elle existe
    // private final UserRepository repository; 

    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Renvoie un utilisateur vide pour que l'app démarre
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities("USER")
                .build();
    }
}