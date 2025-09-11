package com.example.complaint_reporting_api_v2;

import com.example.complaint_reporting_api_v2.dto.auth.LoginRequest;
import com.example.complaint_reporting_api_v2.dto.auth.LoginResponse;
import com.example.complaint_reporting_api_v2.dto.auth.RegisterRequest;
import com.example.complaint_reporting_api_v2.dto.auth.RegisterResponse;
import com.example.complaint_reporting_api_v2.entity.AppUser;
import com.example.complaint_reporting_api_v2.repository.AppUserRepository;
import com.example.complaint_reporting_api_v2.service.AppUserService;
import com.example.complaint_reporting_api_v2.service.JwtService;
import com.example.complaint_reporting_api_v2.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- TEST REGISTER ----------
    @Test
    void register_ShouldSaveUserAndReturnToken() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testUser")
                .password("password123")
                .role("ROLE_USER")
                .build();

        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(eq("testuser"), anyMap())).thenReturn("mockToken");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresInMs());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(userCaptor.capture());
        assertEquals("testuser", userCaptor.getValue().getUsername());
        assertEquals("encodedPassword", userCaptor.getValue().getPasswordHash());
    }

    @Test
    void register_ShouldThrowWhenUsernameExists() {
        RegisterRequest request = RegisterRequest.builder()
                .username("existing")
                .password("123")
                .role("ROLE_USER")
                .build();

        when(appUserRepository.findByUsername("existing")).thenReturn(Optional.of(new AppUser()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldThrowWhenRoleInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("123")
                .role("INVALID_ROLE")
                .build();

        when(appUserRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authService.register(request));
    }

    // ---------- TEST LOGIN ----------
    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        LoginRequest request = LoginRequest.builder()
                .username("testUser")
                .password("password123")
                .build();

        AppUser mockUser = AppUser.builder()
                .username("testuser")
                .roles(Set.of("ROLE_USER"))
                .build();

        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(eq("testuser"), anyMap())).thenReturn("mockLoginToken");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "password123"));

        assertNotNull(response);
        assertEquals("mockLoginToken", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresInMs());
    }

    @Test
    void login_ShouldFallbackToDefaultRoleIfUserNotFound() {
        LoginRequest request = LoginRequest.builder()
                .username("unknown")
                .password("password")
                .build();

        when(appUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(jwtService.generateToken(eq("unknown"), anyMap())).thenReturn("mockFallbackToken");
        when(jwtService.getExpirationMs()).thenReturn(12345L);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockFallbackToken", response.getToken());
    }

    // ---------- TEST TOKEN INFO ----------
    @Test
    void tokenInfo_ShouldReturnUsernameFromToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer mockToken");
        when(jwtService.getUsername("mockToken")).thenReturn("decodedUser");

        String username = authService.tokenInfo(request);

        assertEquals("decodedUser", username);
    }

    @Test
    void tokenInfo_ShouldThrowWhenHeaderInvalid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> authService.tokenInfo(request));
    }
}
