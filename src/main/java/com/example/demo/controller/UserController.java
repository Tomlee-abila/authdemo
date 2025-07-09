package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management endpoints.
 * Provides protected routes for user operations.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get current user profile.
     * @return current user information
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get profile: " + e.getMessage()));
        }
    }
    
    /**
     * Get all users (Admin only).
     * @return list of all users
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }
    
    /**
     * Get user by ID (Admin only).
     * @param id user ID
     * @return user information
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }
    
    /**
     * Enable/disable user account (Admin only).
     * @param id user ID
     * @param enabled enable/disable flag
     * @return updated user
     */
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> setUserEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            User user = userService.setUserEnabled(id, enabled);
            String message = enabled ? "User enabled successfully" : "User disabled successfully";
            return ResponseEntity.ok(ApiResponse.success(message, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }
    
    /**
     * Delete user (Admin only).
     * @param id user ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
}
