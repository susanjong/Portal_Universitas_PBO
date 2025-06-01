package com.example.portaluniv.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.portaluniv.demo.config.CustomUserDetails;

@Controller
public class DashboardController {

    @GetMapping("/dsmahasiswa")
    public String dashboard(Model model) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Add user information to the model
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("fullName", userDetails.getFullName());
            model.addAttribute("authorities", userDetails.getAuthorities());
            
            // Determine user role for different dashboard views
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            model.addAttribute("userRole", role);
        }
        
        return "dsmahasiswa";
    }

    @GetMapping("/Admin_beranda")
    public String adminDashboard(Model model) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Add user information to the model
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("fullName", userDetails.getFullName());
            model.addAttribute("authorities", userDetails.getAuthorities());
            model.addAttribute("userRole", "ADMIN");
        }
        
        return "Admin_beranda"; // ini akan cari Admin_beranda.html di templates/
    }
}