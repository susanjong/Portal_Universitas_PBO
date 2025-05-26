package com.example.portaluniv.demo.dto;

import com.example.portaluniv.demo.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistrationForm {
    
    @NotBlank(message = "Nama lengkap wajib diisi")
    private String name;
    
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    private String email;
    
    @NotBlank(message = "Password wajib diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;
    
    @NotNull(message = "Peran wajib dipilih")
    private User.Role role;
    
    private String username; // For admin role
    
    // Constructors
    public RegistrationForm() {}
    
    public RegistrationForm(String name, String email, String password, User.Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}