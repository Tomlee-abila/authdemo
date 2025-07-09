package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service class for authentication operations.
 * Handles login, registration, and token generation.
 */
@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthService(AuthenticationManager authenticationManager, 
                      UserService userService, 
                      JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Authenticate user and generate JWT token.
     * @param loginRequest login credentials
     * @return authentication response with JWT token
     * @throws BadCredentialsException if credentials are invalid
     */
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );
            
            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsernameOrEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            
            // Return authentication response
            return new AuthResponse(token, user, jwtUtil.getExpirationTime());
            
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/email or password");
        }
    }
    
    /**
     * Register new user and generate JWT token.
     * @param registerRequest registration details
     * @return authentication response with JWT token
     * @throws RuntimeException if registration fails
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Register user
        User user = userService.registerUser(registerRequest);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        // Return authentication response
        return new AuthResponse(token, user, jwtUtil.getExpirationTime());
    }
    
    /**
     * Validate JWT token.
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    /**
     * Get username from JWT token.
     * @param token JWT token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
    
    /**
     * Refresh JWT token.
     * @param token current JWT token
     * @return new authentication response with refreshed token
     * @throws RuntimeException if token is invalid or user not found
     */
    public AuthResponse refreshToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        
        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsernameOrEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String newToken = jwtUtil.generateToken(user);
        return new AuthResponse(newToken, user, jwtUtil.getExpirationTime());
    }
}
