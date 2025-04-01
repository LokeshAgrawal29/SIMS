package com.example.InventryManagement.controllers;

import com.example.InventryManagement.Entities.User;
import com.example.InventryManagement.Entities.UserRole;
import com.example.InventryManagement.Services.UserService;
import com.example.InventryManagement.dto.LoginRequest;
import com.example.InventryManagement.dto.UserRegistrationDTO;
import com.example.InventryManagement.dto.UserDTO;
import com.example.InventryManagement.dto.ErrorResponse;
import com.example.InventryManagement.dto.AuthResponse;
import com.example.InventryManagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        try {
            UserDTO user = userService.findUserDTOByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error fetching user details: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            System.out.println("Received registration request: " + registrationDTO);
            UserDTO newUser = userService.registerUser(registrationDTO);
            
            // Generate token for the new user
            String token = jwtUtil.generateToken(newUser.getEmail());
            
            // Create response with both user and token
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUser(newUser);
            response.setStatus("success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for email: " + loginRequest.getEmail());
            
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                System.out.println("Email or password is null");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email and password are required"));
            }
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            // If we get here, authentication was successful
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication successful for: " + loginRequest.getEmail());
            
            // Get user details
            UserDTO user = userService.findUserDTOByEmail(loginRequest.getEmail());
            System.out.println("User found: " + user.getEmail());
            
            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getEmail());
            System.out.println("Token generated successfully");
            
            // Create response
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUser(user);
            response.setStatus("success");
            
            System.out.println("Login successful for user: " + user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Login failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error checking email: " + e.getMessage()));
        }
    }
    
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logoutUser() {
        // This will be handled by Spring Security
        return ResponseEntity.ok().build();
    }

    // Admin endpoints with path variables
    @PutMapping(value = "/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestParam UserRole newRole) {
        return ResponseEntity.ok(userService.updateUserRole(userId, newRole));
    }
    
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }
    
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    /**
     * Admin-only endpoint to create a new user
     * This is separate from the self-registration endpoint and requires admin privileges
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationDTO userDto) {
        try {
            UserDTO newUser = userService.registerUser(userDto);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error creating user: " + e.getMessage()));
        }
    }
}
