package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        // Only hash the password if it's not already hashed
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !isPasswordHashed(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // Method to check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
    private boolean isPasswordHashed(String password) {
        return password != null && password.matches("^\\$2[aby]\\$.*");
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}