package com.example.complaint_reporting_api_v2.service.impl;

import com.example.complaint_reporting_api_v2.dto.auth.*;
import com.example.complaint_reporting_api_v2.entity.AppUser;
import com.example.complaint_reporting_api_v2.entity.AppUserRoleEnum;
import com.example.complaint_reporting_api_v2.repository.AppUserRepository;
import com.example.complaint_reporting_api_v2.service.AuthService;
import com.example.complaint_reporting_api_v2.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ){
        this.appUserRepository=appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request){
        // Get username from request
        String username = request.getUsername().trim().toLowerCase();
        appUserRepository.findByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });
        // Check request role
        AppUserRoleEnum appUserRole;
        try {
            appUserRole = AppUserRoleEnum.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role value");
        }
        // Set user role
        String roles = request.getRole().toUpperCase();
        // Create user
        AppUser user = AppUser.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(roles)))
                .build();
        appUserRepository.save(user);
        // Get token
        String token = jwtService.generateToken(user.getUsername(), Map.of("roles", user.getRoles()));
        return RegisterResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }

    public String tokenInfo(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        return  jwtService.getUsername(token);
    }

    public LoginResponse login(LoginRequest request) {

        // Find user with request.username and request.password
        String username = request.getUsername().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));

        // Take roles from DB if no user is found, assume USER
        Set<String> roles = appUserRepository.findByUsername(username)
                .map(AppUser::getRoles)
                .orElseGet(() -> new HashSet<>(Collections.singletonList("ROLE_USER")));

        // Generate token
        String token = jwtService.generateToken(
                username,
                Map.of("roles", roles));

        // Return
        return LoginResponse.builder()
                .token(token).tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .build();
    }
}
