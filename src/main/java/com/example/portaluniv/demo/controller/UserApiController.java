package com.example.portaluniv.demo.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Validasi jika username sudah ada
            if (userService.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Username sudah digunakan"));
            }
            
            // Validasi jika email sudah ada
            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Email sudah digunakan"));
            }
            
            User savedUser = userService.save(user);
            return ResponseEntity.ok().body(Map.of("success", true, "data", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
        try {
            // Ambil user yang sudah ada dari database
            Optional<User> existingUserOpt = userService.findById(id);
            if (!existingUserOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "User tidak ditemukan"));
            }
            
            User existingUser = existingUserOpt.get();
            
            // Update field yang dibutuhkan
            existingUser.setUsername(userRequest.getUsername());
            existingUser.setName(userRequest.getName());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setRole(userRequest.getRole());
            
            // Jangan update password jika tidak ada di request
            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                existingUser.setPassword(userRequest.getPassword());
            }
            
            User updatedUser = userService.save(existingUser);
            return ResponseEntity.ok().body(Map.of("success", true, "data", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Cek apakah user dengan id tersebut ada
            if (!userService.findById(id).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "User tidak ditemukan"));
            }
            
            userService.deleteById(id);
            return ResponseEntity.ok().body(Map.of("success", true, "message", "User berhasil dihapus"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}