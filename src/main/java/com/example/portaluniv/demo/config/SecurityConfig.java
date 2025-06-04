package com.example.portaluniv.demo.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.portaluniv.demo.service.CustomUserDetailsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                              HttpServletResponse response,
                                              Authentication authentication) throws IOException, ServletException {
                
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                String redirectUrl = "/";
                
                for (GrantedAuthority authority : authorities) {
                    String role = authority.getAuthority();
                    
                    if (role.equals("ROLE_ADMIN")) {
                        redirectUrl = "/Admin_beranda";
                        break;
                    } else if (role.equals("ROLE_DOSEN")) {
                        redirectUrl = "/dosen_dashboard_beranda";  // Sesuaikan dengan halaman dosen
                        break;
                    } else if (role.equals("ROLE_MAHASISWA")) {
                        redirectUrl = "/dashboard_mahasiswa_beranda";
                        break;
                    }
                }
                
                response.sendRedirect(redirectUrl);
            }
        };
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
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler())  
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home")
                .permitAll()
            )
            .userDetailsService(customUserDetailsService);

        return http.build();
    }
}