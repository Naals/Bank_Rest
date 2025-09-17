package com.aslan.project.bank_rest.security.jwt;

import com.aslan.project.bank_rest.entity.User;
import com.aslan.project.bank_rest.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    public CustomUserDetailsService(UserRepository repo) { this.userRepo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = java.util.Arrays.stream(u.getRoles().name().split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPasswordHash(), u.isEnabled(),
                true, true, true, authorities);
    }
}

