package com.example.complaint_reporting_api_v2.config;

import com.example.complaint_reporting_api_v2.service.AppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final AppUserService appUserService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(AppUserService appUserService, JwtAuthFilter jwtAuthFilter){
        this.appUserService=appUserService;
        this.jwtAuthFilter=jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (since we are using JWT, not cookies/sessions)
                .csrf(csrf -> csrf.disable())
                // Make session stateless (JWT will handle authentication instead of sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define which endpoints are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // Allow actuator health endpoint without authentication
                        .requestMatchers("/actuator/health").permitAll()
                        // Allow register and login endpoint without authentication
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // Allow endpoint based on role
                        .requestMatchers(HttpMethod.POST, "/api/complaints").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/complaints").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/complaints/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/complaints/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("USER")
                        // Any other endpoint must be authenticated
                        .anyRequest().authenticated()
                )
                // Use custom authentication provider (DAO + PasswordEncoder)
                .authenticationProvider(daoAuthenticationProvider())
                // Add our JWT filter before the UsernamePasswordAuthenticationFilter (so it can validate tokens before default authentication logic runs)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // DAO provider integrates UserDetailsService + PasswordEncoder
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Expose AuthenticationManager bean (needed for login endpoint)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // BCrypt with strength 12 (the higher the number, the more secure but slower)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
