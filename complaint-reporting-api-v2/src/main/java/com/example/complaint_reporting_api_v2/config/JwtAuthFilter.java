package com.example.complaint_reporting_api_v2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.complaint_reporting_api_v2.service.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

@Component
//@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService){
        this.jwtService=jwtService;
        this.userDetailsService=userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException
    {
        // Extract authorization header from request
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        // Extract the token (remove the "Bearer " prefix)
        String token = authHeader.substring(7);

        try {
            // Get the username from the JWT
            String username = jwtService.getUsername(token);
            // If username is valid and no authentication exists yet in the security context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from database
                UserDetails user = userDetailsService.loadUserByUsername(username);
                // Create an authentication object using the user details and authorities
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                // Attach additional details from the request
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the authentication object into the SecurityContex
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ignored) {
            // token invalid / expired
            System.out.println("Catched");
        }
        // Continue with the rest of the filter chain
        chain.doFilter(request, response);
    }

}
