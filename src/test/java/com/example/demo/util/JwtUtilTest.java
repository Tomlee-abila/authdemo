package com.example.demo.util;

import com.example.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }
    
    @Test
    void generateToken_ValidUser_ReturnsToken() {
        String token = jwtUtil.generateToken(testUser);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }
    
    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String token = jwtUtil.generateToken(testUser);
        
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }
    
    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken(testUser);
        
        boolean isValid = jwtUtil.validateToken(token, testUser);
        
        assertTrue(isValid);
    }
    
    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtUtil.validateToken(invalidToken);
        
        assertFalse(isValid);
    }
    
    @Test
    void extractExpiration_ValidToken_ReturnsExpirationDate() {
        String token = jwtUtil.generateToken(testUser);
        
        assertNotNull(jwtUtil.extractExpiration(token));
    }
    
    @Test
    void getExpirationTime_ReturnsConfiguredExpiration() {
        Long expiration = jwtUtil.getExpirationTime();
        
        assertEquals(86400000L, expiration);
    }
}
