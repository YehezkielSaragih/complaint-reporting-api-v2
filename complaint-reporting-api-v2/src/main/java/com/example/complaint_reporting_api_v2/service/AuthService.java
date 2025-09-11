package com.example.complaint_reporting_api_v2.service;

import com.example.complaint_reporting_api_v2.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    RegisterResponse register(RegisterRequest req);
    String tokenInfo(HttpServletRequest request);
    LoginResponse login(LoginRequest req);
}
