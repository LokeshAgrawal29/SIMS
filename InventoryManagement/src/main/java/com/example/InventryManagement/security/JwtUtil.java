package com.example.InventryManagement.security;

import com.example.InventryManagement.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private Key key;
    private Long expiration;

    // Constructor for normal use (values injected from application.properties)
    public JwtUtil(
            @Value("${jwt.expiration}") Long expiration,
            @Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // No-args constructor for testing (default values)
    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor("your-256-bit-secret-key-here-must-be-longer-than-256-bits".getBytes());
        this.expiration = 3600L; // Default expiration time (1 hour)
    }

    // Setters for testing
    public void setSecret(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    // Generate JWT token with claims
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        return createToken(claims, user.getEmail());
    }
    
    // Generate token from email - used for login
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    // Create a JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            System.out.println("Validating token for username: " + username);
            System.out.println("UserDetails username: " + userDetails.getUsername());
            System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
            
            boolean isExpired = isTokenExpired(token);
            System.out.println("Token expired: " + isExpired);
            
            boolean usernameMatch = username.equals(userDetails.getUsername());
            System.out.println("Username match: " + usernameMatch);
            
            return (usernameMatch && !isExpired);
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Extract username from token
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            System.out.println("Extracted username from token: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("Error extracting username from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            System.out.println("Token expiration date: " + expiration);
            return expiration;
        } catch (Exception e) {
            System.out.println("Error extracting expiration from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Extract specific claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
