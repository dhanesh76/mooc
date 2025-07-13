package com.dhanesh.auth.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dhanesh.auth.portal.security.jwt.JwtAuthenticationFilter;
import com.dhanesh.auth.portal.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;

/**
 * Application-level Spring Configuration for authentication-related beans,
 * such as AuthenticationManager, PasswordEncoder, JWT filter, etc.
 * 
 * All security dependencies are centrally configured here.
 */

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Bean for password encoding using BCrypt algorithm.
     * Ensures passwords are stored securely in the database.
     */
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DAO-based AuthenticationProvider that uses the custom UserDetailsService
     * and the defined PasswordEncoder for authentication logic.
     */
    @Bean 
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    } 

    /**
     * AuthenticationManager bean provided via AuthenticationConfiguration.
     * Required for programmatic authentication (e.g., during login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * JWT filter that intercepts requests and performs token validation.
     * Added to the security filter chain for request-based security.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    } 
 }
