package com.example.InventryManagement.config;

import com.example.InventryManagement.repositories.UserRepository;
import com.example.InventryManagement.security.CustomUserDetailsService;
import com.example.InventryManagement.security.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public JwtUtil testJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        jwtUtil.setSecret("test_jwt_secret_key_for_testing_purposes_only_very_long_key_for_testing");
        jwtUtil.setExpiration(86400L);
        return jwtUtil;
    }

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }
} 