package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
        
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
    }
    
    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        
        UserDetails result = userService.loadUserByUsername("testuser");
        
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsernameOrEmail("testuser", "testuser");
    }
    
    @Test
    void loadUserByUsername_UserNotExists_ThrowsException() {
        when(userRepository.findByUsernameOrEmail("nonexistent", "nonexistent"))
                .thenReturn(Optional.empty());
        
        assertThrows(UsernameNotFoundException.class, 
                () -> userService.loadUserByUsername("nonexistent"));
    }
    
    @Test
    void registerUser_ValidRequest_ReturnsUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        User result = userService.registerUser(registerRequest);
        
        assertNotNull(result);
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_PasswordsDoNotMatch_ThrowsException() {
        registerRequest.setConfirmPassword("differentPassword");
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.registerUser(registerRequest));
        
        assertEquals("Passwords do not match", exception.getMessage());
    }
    
    @Test
    void registerUser_UsernameExists_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.registerUser(registerRequest));
        
        assertEquals("Username is already taken", exception.getMessage());
    }
    
    @Test
    void registerUser_EmailExists_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);
        
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.registerUser(registerRequest));
        
        assertEquals("Email is already registered", exception.getMessage());
    }
    
    @Test
    void findByUsername_UserExists_ReturnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        Optional<User> result = userService.findByUsername("testuser");
        
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
    
    @Test
    void findByUsername_UserNotExists_ReturnsEmpty() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        
        Optional<User> result = userService.findByUsername("nonexistent");
        
        assertFalse(result.isPresent());
    }
}
