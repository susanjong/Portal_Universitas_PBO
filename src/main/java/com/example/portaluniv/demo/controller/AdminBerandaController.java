package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.portaluniv.demo.config.CustomUserDetails;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class AdminBerandaController {

    @Autowired
    private UserService userService;

    @GetMapping("/Admin_beranda")
    public String adminDashboard(Model model, 
                           @RequestParam(required = false) String showAddForm,
                           @RequestParam(required = false) Long editUserId,
                           @RequestParam(required = false) String usernameFilter,
                           @RequestParam(required = false) String nameFilter,
                           @RequestParam(required = false) String roleFilter) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }

        List<User> allUsers = userService.findAll();

        //Filter untuk beranda
        if (usernameFilter != null && !usernameFilter.trim().isEmpty()) {
            allUsers = allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(usernameFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            allUsers = allUsers.stream()
                .filter(user -> user.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (roleFilter != null && !roleFilter.trim().isEmpty()) {
            allUsers = allUsers.stream()
                .filter(user -> user.getRole().toString().equals(roleFilter))
                .collect(Collectors.toList());
        }

        model.addAttribute("users", allUsers);
        if ("true".equals(showAddForm)) {
            model.addAttribute("showAddForm", true);
        }

        if (editUserId != null) {
            Optional<User> userToEdit = userService.findById(editUserId);
            if (userToEdit.isPresent()) {
                model.addAttribute("editUser", userToEdit.get());
                model.addAttribute("showEditForm", true);
            }
        }

        model.addAttribute("usernameFilter", usernameFilter);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("roleFilter", roleFilter);
        
        return "Admin_beranda";
    }

    @PostMapping("/Admin_beranda/add-user")
    public String addUser(@RequestParam String username,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String role,
                         @RequestParam String password,
                         RedirectAttributes redirectAttributes) {
        try {
            // Validasi username sudah ada
            if (userService.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username sudah digunakan");
                return "redirect:/Admin_beranda";
            }
            
            // Validasi email sudah ada
            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email sudah digunakan");
                return "redirect:/Admin_beranda";
            }
            
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setRole(User.Role.valueOf(role.toUpperCase()));
            newUser.setPassword(password);
            
            userService.save(newUser);
            redirectAttributes.addFlashAttribute("success", "User berhasil ditambahkan");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_beranda";
    }

    @PostMapping("/Admin_beranda/edit-user")
    public String editUser(@RequestParam Long id,
                          @RequestParam String username,
                          @RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String role,
                          RedirectAttributes redirectAttributes) {
        try {
            Optional<User> existingUserOpt = userService.findById(id);
            if (!existingUserOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "User tidak ditemukan");
                return "redirect:/Admin_beranda";
            }
            
            User existingUser = existingUserOpt.get();
            existingUser.setUsername(username);
            existingUser.setName(name);
            existingUser.setEmail(email);
            existingUser.setRole(User.Role.valueOf(role.toUpperCase()));
            
            userService.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "User berhasil diupdate");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_beranda";
    }

    @PostMapping("/Admin_beranda/delete-user")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!userService.findById(id).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "User tidak ditemukan");
                return "redirect:/Admin_beranda";
            }
            
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User berhasil dihapus");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_beranda";
    }
}