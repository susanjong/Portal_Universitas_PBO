package com.example.portaluniv.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.portaluniv.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(authz -> authz
          // allow everything under /css/, /js/, /images/
          .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
          
          // public pages
          .requestMatchers("/", "/home", "/register", "/login").permitAll()
          
          // all other endpoints require authentication
          .anyRequest().authenticated()
      )
      // your login/logout config (if any) goes here
      .formLogin(form -> form
          .loginPage("/login")
          .permitAll()
      )
      .logout(logout -> logout
          .permitAll()
      )
      .userDetailsService(customUserDetailsService);

    return http.build();
}
}