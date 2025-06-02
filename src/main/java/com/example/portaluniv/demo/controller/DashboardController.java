package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class DashboardController {

    @Autowired
    private UserService userService; // Inject UserService

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dsmahasiswa")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("fullName", userDetails.getFullName());
            model.addAttribute("authorities", userDetails.getAuthorities());
            
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("userRole", role);
        }
        
        return "dsmahasiswa";
    }

    @GetMapping("/Admin_beranda")
    public String adminDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Ganti UserService.findByUsername() dengan userService.findByUsername()
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
        model.addAttribute("users", allUsers);
        
        return "Admin_beranda";
    }

    @GetMapping("/Admin_profile")
    public String adminProfile(Model model) {
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
        
        return "Admin_profile"; // return template Admin_profile.html
    }

    @PostMapping("/Admin_profile/updatePassword")
    public String updatePassword(@RequestParam String password, 
                            @RequestParam String confirmPassword, 
                            RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Password tidak cocok!");
                return "redirect:/Admin_profile";
            }
            
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter!");
                return "redirect:/Admin_profile";
            }
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                user.setPassword(password); 
                userService.save(user);     
                
                redirectAttributes.addFlashAttribute("success", "Password berhasil diupdate!");
            }
        }
        
        return "redirect:/Admin_profile";
    }

    @GetMapping("/Admin_daftarkelas")
    public String admindaftarkelas(Model model) {
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
        
        return "Admin_daftarkelas";
    }

    @GetMapping("/Admin_aturkelas")
    public String adminaturkelas(Model model) {
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
        
        return "Admin_aturkelas";
    }
}